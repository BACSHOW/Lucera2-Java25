package services.randomcraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.mail.MailService;
import services.mail.MailText;
import services.randomcraft.RandomCraftConfig.Extract;
import services.randomcraft.RandomCraftConfig.Reward;
import services.randomcraft.RandomCraftData.Slot;

/**
 * Random Craft (janelas RandomCraftWndTest e RandomCraftChargingWnd do cliente).
 *
 * 5 casas sorteadas do pool do randomcraft.properties. Dá para travar casas
 * (ficam na próxima atualização), atualizar a lista (gasta Craft Points + item)
 * e criar: o servidor sorteia uma das 5 casas e entrega o item. Craft Points
 * vêm de extrair itens (experiência de craft: MaxExp = 1 ponto).
 *
 * Conversa com a janela:
 *   cliente  -> servidor: bypass "scripts_services.RandomCraft:rc..."
 *   servidor -> cliente: pacote InterfaceData, canal "RCRAFT":
 *     OPEN
 *     STATE  pontos|exp|maxExp|maxPontos|maxTravas|ptsAtualizar|itemAtualizar|qtdAtualizar|temAtualizar|
 *            itemCriar|qtdCriar|temCriar|itemTrava|temTrava|ptsCriar|casa0|..|casa4|maxCasasTravadas
 *            casa = id,qtd,grau,encantamento,travada,vezesTravada,custoTrava
 *            (vezesTravada: +1 a cada travar e +1 a cada atualização com a casa travada; ao chegar
 *            em maxTravas a casa não pode mais ser travada. No máximo maxCasasTravadas ao mesmo tempo)
 *     RESULT id|qtd|grau|encantamento
 *     LIST   n|id,qtd,encantamento,expPorUnidade,adenaPorUnidade|...   (vem depois de um PTS)
 *     PREV   expGanha|adena|pontosNovos|expNova|maxExp|pontos|exp|excede|selecaoCortada
 *     PTS    pontos|exp|maxExp|maxPontos
 *     OK     op|texto
 *     FAIL   op|texto
 */
public final class RandomCraftService
{
	private static final Logger _log = LoggerFactory.getLogger(RandomCraftService.class);

	public static final String CHANNEL = "RCRAFT";
	private static final long ACTION_DELAY_MS = 300L;

	static RandomCraftStore STORE = new RandomCraftStore.Jdbc();

	private static final Map<Integer, RandomCraftData> CACHE = new ConcurrentHashMap<>();
	private static final Map<Integer, Long> LAST_ACTION = new ConcurrentHashMap<>();

	private RandomCraftService()
	{
	}

	// ── envio ─────────────────────────────────────────────────────────

	public static void ui(Player p, String type, String data)
	{
		if (p != null)
			p.sendPacket(new TutorialShowHtml(CHANNEL + "|" + type + "|" + data));
	}

	private static void ok(Player p, String op, String text)
	{
		ui(p, "OK", op + "|" + MailText.out(text));
	}

	private static void fail(Player p, String op, String text)
	{
		ui(p, "FAIL", op + "|" + MailText.out(text));
	}

	private static void screen(Player p, String text)
	{
		p.sendPacket((IStaticPacket) new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
	}

	private static boolean throttle(Player p)
	{
		long t = System.currentTimeMillis();
		Long last = LAST_ACTION.put(p.getObjectId(), t);
		return last == null || t - last >= ACTION_DELAY_MS;
	}

	static String busy(Player p)
	{
		if (p.isInStoreMode())
			return "Feche a loja particular primeiro.";
		if (p.isInTrade() || p.isProcessingRequest())
			return "Termine a troca primeiro.";
		if (p.getEnchantScroll() != null)
			return "Termine o encantamento primeiro.";
		if (p.isFishing())
			return "Pare de pescar primeiro.";
		if (p.isOlyParticipant())
			return "Não é possível durante a Olimpíada.";
		if (p.isDead())
			return "Não é possível estando morto.";
		if (p.getVarB("jailed"))
			return "Não é possível na prisão.";
		return null;
	}

	private static String check(Player p)
	{
		if (!RandomCraftConfig.ENABLED)
			return "O Random Craft está desligado.";
		return busy(p);
	}

	// ── dados ─────────────────────────────────────────────────────────

	public static RandomCraftData get(int objId)
	{
		RandomCraftData d = CACHE.get(objId);
		if (d != null)
			return d;
		try
		{
			d = STORE.load(objId);
		}
		catch (Exception e)
		{
			_log.warn("RandomCraft: não consegui ler o personagem " + objId + ": " + e.getMessage());
			return null;
		}
		if (d == null)
		{
			d = new RandomCraftData(objId);
			d.fresh = true;
		}
		RandomCraftData old = CACHE.putIfAbsent(objId, d);
		return old != null ? old : d;
	}

	private static void save(RandomCraftData d)
	{
		try
		{
			STORE.save(d);
		}
		catch (Exception e)
		{
			_log.warn("RandomCraft: não consegui gravar o personagem " + d.objId + ": " + e.getMessage());
		}
	}

	public static void onExit(Player p)
	{
		CACHE.remove(p.getObjectId());
		LAST_ACTION.remove(p.getObjectId());
	}

	// ── custos ────────────────────────────────────────────────────────

	private static String name(int id)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(id);
		return t != null ? t.getName() : ("item " + id);
	}

	private static String fmt(long n)
	{
		return String.format("%,d", n).replace(',', '.');
	}

	private static long have(Player p, int id)
	{
		return id <= 0 ? 0L : p.getInventory().getCountOf(id);
	}

	/** Falta algo para pagar? null = dá. */
	private static String lacks(Player p, int points, RandomCraftData d, int itemId, long itemCount)
	{
		if (d.points < points)
			return "Você precisa de " + points + " Craft Points.";
		if (itemId > 0 && have(p, itemId) < itemCount)
			return "Você precisa de " + fmt(itemCount) + " " + name(itemId) + ".";
		return null;
	}

	/** Cobra (depois de lacks). false = não conseguiu tirar o item. */
	private static boolean pay(Player p, RandomCraftData d, int points, int itemId, long itemCount)
	{
		if (itemId > 0)
		{
			if (!p.getInventory().destroyItemByItemId(itemId, itemCount))
				return false;
			Log.LogItem(p, Log.ItemLog.Delete, itemId, itemCount);
			p.sendPacket((IStaticPacket) SystemMessage2.removeItems(itemId, itemCount));
		}
		d.points -= points;
		return true;
	}

	private static void refund(Player p, RandomCraftData d, int points, int itemId, long itemCount)
	{
		d.points += points;
		if (itemId > 0)
			p.getInventory().addItem(itemId, itemCount);
	}

	static int lockedNow(RandomCraftData d)
	{
		int n = 0;
		for (Slot s : d.slots)
			if (s.locked && s.id > 0)
				n++;
		return n;
	}

	/** Custo de travar mais uma casa agora: sobe com quantas já estão travadas (1a, 2a, 3a...). */
	static long lockCost(RandomCraftData d)
	{
		return RandomCraftConfig.LOCK_ITEM_COUNT + RandomCraftConfig.LOCK_ITEM_STEP * lockedNow(d);
	}

	// ── sorteio ───────────────────────────────────────────────────────

	/** Sorteia uma recompensa pelo peso, evitando itens que já estão nas casas (quando dá). */
	static Reward roll(List<Reward> pool, List<Integer> avoid)
	{
		Reward pick = null;
		for (int t = 0; t < 30; t++)
		{
			pick = weighted(pool);
			if (pick != null && !avoid.contains(pick.id))
				return pick;
		}
		return pick;
	}

	private static Reward weighted(List<Reward> pool)
	{
		long total = 0;
		for (Reward r : pool)
			total += r.weight;
		if (total <= 0)
			return null;
		long x = Rnd.get(total);
		for (Reward r : pool)
		{
			x -= r.weight;
			if (x < 0)
				return r;
		}
		return pool.get(pool.size() - 1);
	}

	/** Preenche as casas não travadas. */
	static void fill(RandomCraftData d)
	{
		List<Reward> pool = RandomCraftConfig.POOL;
		List<Integer> avoid = new ArrayList<>();
		for (Slot s : d.slots)
			if (s.locked && s.id > 0)
				avoid.add(s.id);
		for (Slot s : d.slots)
		{
			if (s.locked && s.id > 0)
				continue;
			Reward r = roll(pool, avoid);
			if (r == null)
				continue;
			s.id = r.id;
			s.count = r.count;
			s.grade = r.grade;
			s.enchant = r.enchant;
			s.locked = false;
			avoid.add(r.id);
		}
	}

	// ── estado ────────────────────────────────────────────────────────

	private static String stateText(Player p, RandomCraftData d)
	{
		StringBuilder b = new StringBuilder();
		b.append(d.points).append('|').append(d.exp).append('|').append(RandomCraftConfig.MAX_EXP).append('|').append(RandomCraftConfig.MAX_POINTS).append('|')
			.append(RandomCraftConfig.MAX_LOCKS).append('|').append(RandomCraftConfig.REFRESH_POINTS).append('|')
			.append(RandomCraftConfig.REFRESH_ITEM_ID).append('|').append(RandomCraftConfig.REFRESH_ITEM_COUNT).append('|')
			.append(have(p, RandomCraftConfig.REFRESH_ITEM_ID)).append('|')
			.append(RandomCraftConfig.CREATE_ITEM_ID).append('|').append(RandomCraftConfig.CREATE_ITEM_COUNT).append('|')
			.append(have(p, RandomCraftConfig.CREATE_ITEM_ID)).append('|')
			.append(RandomCraftConfig.LOCK_ITEM_ID).append('|').append(have(p, RandomCraftConfig.LOCK_ITEM_ID)).append('|')
			.append(RandomCraftConfig.CREATE_POINTS);
		for (Slot s : d.slots)
			b.append('|').append(s.id).append(',').append(s.count).append(',').append(s.grade).append(',').append(s.enchant).append(',')
				.append(s.locked ? 1 : 0).append(',').append(s.lockCount).append(',').append(lockCost(d));
		b.append('|').append(RandomCraftConfig.MAX_LOCKED_SLOTS);
		return b.toString();
	}

	public static void open(Player p)
	{
		if (!RandomCraftConfig.ENABLED)
		{
			p.sendMessage("O Random Craft está desligado.");
			return;
		}
		ui(p, "OPEN", "");
	}

	// ── janela de prêmios ─────────────────────────────────────────────

	private static final RandomCraftPrizes.Resolver PRIZE_RESOLVER = new RandomCraftPrizes.Resolver()
	{
		@Override
		public RandomCraftPrizes.Info info(int itemId)
		{
			ItemTemplate t = ItemHolder.getInstance().getTemplate(itemId);
			if (t == null)
				return null;
			String name = t.getName();
			String add = t.getAdditionalName();
			if (add != null && !add.isEmpty())
				name = name + " - " + add;
			int kind = t.isWeapon() ? RandomCraftPrizes.KIND_WEAPON : (t.isArmor() || t.isAccessory()) ? RandomCraftPrizes.KIND_ARMOR : RandomCraftPrizes.KIND_OTHER;
			return new RandomCraftPrizes.Info(name, t.getIcon(), kind);
		}
	};

	/** Janela nativa de prêmios: manda o pacote PRIZES (página e filtro). */
	public static void prizeList(Player p, String[] a)
	{
		if (!RandomCraftConfig.ENABLED)
		{
			fail(p, "prizes", "O Random Craft está desligado.");
			return;
		}
		int page = 1;
		try
		{
			if (a != null && a.length > 0)
				page = Integer.parseInt(a[0].trim());
		}
		catch (Exception e)
		{
			page = 1;
		}
		String filter = a != null && a.length > 1 ? a[1] : "all";
		ui(p, "PRIZES", RandomCraftPrizes.packet(RandomCraftConfig.POOL, PRIZE_RESOLVER, page, filter));
	}

	/** .craftprizes: manda a janela de prêmios abrir (cliente). */
	public static void openPrizes(Player p)
	{
		if (!RandomCraftConfig.ENABLED)
		{
			p.sendMessage("O Random Craft está desligado.");
			return;
		}
		ui(p, "PRIZEOPEN", "");
	}

	/** Lista de prêmios (HTML): args = página e filtro (all, weapon, armor, other). */
	public static void prizes(Player p, String[] a)
	{
		if (!RandomCraftConfig.ENABLED)
		{
			p.sendMessage("O Random Craft está desligado.");
			return;
		}
		int page = 1;
		try
		{
			if (a != null && a.length > 0)
				page = Integer.parseInt(a[0].trim());
		}
		catch (Exception e)
		{
			page = 1;
		}
		String filter = a != null && a.length > 1 ? a[1] : "all";
		String html = RandomCraftPrizes.html(RandomCraftConfig.POOL, PRIZE_RESOLVER, page, filter);
		p.sendPacket((IStaticPacket) new NpcHtmlMessage(5).setHtml(html));
	}

	/** A janela abriu (ou precisa redesenhar): a lista vazia é sorteada de graça. */
	public static void state(Player p)
	{
		String why = check(p);
		RandomCraftData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Random Craft indisponível no momento. Tente de novo.";
		if (why != null)
		{
			fail(p, "state", why);
			return;
		}
		boolean saved = false;
		String text;
		synchronized (d)
		{
			if (d.isEmpty() && d.fresh)
			{
				fill(d);
				saved = true;
			}
			d.fresh = false;
			text = stateText(p, d);
		}
		if (saved)
			save(d);
		ui(p, "STATE", text);
	}

	// ── atualizar ─────────────────────────────────────────────────────

	public static void refresh(Player p)
	{
		if (!throttle(p))
			return;
		String why = check(p);
		RandomCraftData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Random Craft indisponível no momento. Tente de novo.";
		if (why != null)
		{
			fail(p, "refresh", why);
			return;
		}
		boolean done = false;
		synchronized (d)
		{
			boolean all = true;
			for (Slot s : d.slots)
				if (!s.locked)
					all = false;
			if (all && !d.isEmpty())
				why = "Todas as casas estão travadas.";
			else
			{
				why = lacks(p, RandomCraftConfig.REFRESH_POINTS, d, RandomCraftConfig.REFRESH_ITEM_ID, RandomCraftConfig.REFRESH_ITEM_COUNT);
				if (why == null && !pay(p, d, RandomCraftConfig.REFRESH_POINTS, RandomCraftConfig.REFRESH_ITEM_ID, RandomCraftConfig.REFRESH_ITEM_COUNT))
					why = "Não foi possível cobrar. Tente de novo.";
				if (why == null)
				{
					// cada atualização com a casa travada gasta uma das travas dela
					for (Slot s : d.slots)
						if (s.locked && s.id > 0)
							s.lockCount = Math.min(RandomCraftConfig.MAX_LOCKS, s.lockCount + 1);
					fill(d);
					// gastou todas: o item ficou desta vez, a casa solta
					for (Slot s : d.slots)
						if (s.locked && s.lockCount >= RandomCraftConfig.MAX_LOCKS)
							s.locked = false;
					done = true;
				}
			}
		}
		if (done)
			save(d);
		else
			fail(p, "refresh", why);
		state(p);
	}

	// ── travar ────────────────────────────────────────────────────────

	/** Trava a casa (cobra o item); travada, destrava de graça. */
	public static void lock(Player p, int n)
	{
		if (!throttle(p))
			return;
		String why = check(p);
		RandomCraftData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Random Craft indisponível no momento. Tente de novo.";
		if (why == null && (n < 0 || n >= RandomCraftConfig.SLOTS))
			why = "Casa inválida.";
		if (why != null)
		{
			fail(p, "lock", why);
			return;
		}
		boolean done = false;
		synchronized (d)
		{
			Slot s = d.slots[n];
			if (s.locked)
			{
				s.locked = false;
				done = true;
			}
			else if (s.id <= 0)
				why = "Essa casa está vazia.";
			else if (s.lockCount >= RandomCraftConfig.MAX_LOCKS)
				why = "Essa casa já usou as " + RandomCraftConfig.MAX_LOCKS + " travas.";
			else if (lockedNow(d) >= RandomCraftConfig.MAX_LOCKED_SLOTS)
				why = "Só dá para travar " + RandomCraftConfig.MAX_LOCKED_SLOTS + " itens da lista ao mesmo tempo.";
			else
			{
				long cost = lockCost(d);
				why = lacks(p, 0, d, RandomCraftConfig.LOCK_ITEM_ID, cost);
				if (why == null && !pay(p, d, 0, RandomCraftConfig.LOCK_ITEM_ID, cost))
					why = "Não foi possível cobrar. Tente de novo.";
				if (why == null)
				{
					s.locked = true;
					s.lockCount++;
					done = true;
				}
			}
		}
		if (done)
			save(d);
		else
			fail(p, "lock", why);
		state(p);
	}

	// ── criar ─────────────────────────────────────────────────────────

	public static void create(Player p)
	{
		if (!throttle(p))
			return;
		String why = check(p);
		RandomCraftData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Random Craft indisponível no momento. Tente de novo.";
		if (why != null)
		{
			fail(p, "create", why);
			return;
		}
		String result = null;
		Reward won = null;
		boolean byMail = false;
		synchronized (d)
		{
			if (d.isEmpty())
				why = "Não há itens na lista. Clique em Register Item para atualizar.";
			else
			{
				int pts = RandomCraftConfig.CREATE_POINTS;
				int itemId = RandomCraftConfig.CREATE_ITEM_ID;
				long itemCount = RandomCraftConfig.CREATE_ITEM_COUNT;
				why = lacks(p, pts, d, itemId, itemCount);
				Slot s = null;
				if (why == null)
				{
					List<Slot> filled = new ArrayList<>();
					for (Slot x : d.slots)
						if (x.id > 0)
							filled.add(x);
					s = filled.get(Rnd.get(filled.size()));
					won = new Reward(s.id, s.count, s.grade, 1, s.enchant);
					String room = fit(p.getInventory(), won.id, won.count);
					if (room != null && !RandomCraftConfig.MAIL_WHEN_FULL)
						why = room;
					else if (!pay(p, d, pts, itemId, itemCount))
						why = "Não foi possível cobrar. Tente de novo.";
					else
					{
						// Grava a lista limpa (e os pontos já descontados) ANTES de entregar: se o banco não
						// gravar, nada é entregue e tudo volta. Na ordem antiga, uma queda do servidor entre a
						// entrega e a gravação deixava a mesma lista no banco para criar de novo.
						String before = d.encodeSlots();
						d.clearSlots();
						boolean stored;
						try
						{
							STORE.save(d);
							stored = true;
						}
						catch (Exception e)
						{
							_log.warn("RandomCraft: não consegui gravar a criação de " + p.getName() + ": " + e.getMessage());
							stored = false;
						}
						if (!stored)
						{
							d.decodeSlots(before);
							refund(p, d, pts, itemId, itemCount);
							why = "Não foi possível concluir agora. Nada foi cobrado; tente de novo.";
						}
						else if (room == null)
						{
							give(p, won);
							result = won.id + "|" + won.count + "|" + won.grade + "|" + won.enchant;
						}
						else if (mail(p, won, room))
						{
							byMail = true;
							result = won.id + "|" + won.count + "|" + won.grade + "|" + won.enchant;
						}
						else
						{
							d.decodeSlots(before);
							refund(p, d, pts, itemId, itemCount);
							save(d);
							why = "Não foi possível entregar agora. Tente de novo.";
						}
					}
				}
			}
		}
		if (result == null)
		{
			fail(p, "create", why);
			state(p);
			return;
		}
		if (won.grade >= 3)
			screen(p, "Random Craft: " + name(won.id) + "!");
		if (byMail)
			ok(p, "mail", "Seu inventário estava sem espaço, então o item veio pelo correio.");
		ui(p, "RESULT", result);
	}

	// ── entrega ───────────────────────────────────────────────────────

	static String fit(PcInventory inv, int id, long count)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(id);
		if (t == null)
			return "Item inválido.";
		long weight = count * (long) t.getWeight();
		long slots;
		if (t.isStackable())
		{
			ItemInstance have = inv.getItemByItemId(id);
			slots = have == null ? 1 : 0;
			if ((have == null ? 0L : have.getCount()) + count > Integer.MAX_VALUE)
				return "Você já tem o máximo de " + t.getName() + " que dá para carregar.";
		}
		else
			slots = count;
		if (!inv.validateWeight(weight))
			return "Peso demais: libere peso no inventário.";
		if (!inv.validateCapacity(slots))
			return "Inventário cheio: libere " + slots + (slots == 1 ? " espaço." : " espaços.");
		return null;
	}

	private static void give(Player p, Reward r)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(r.id);
		PcInventory inv = p.getInventory();
		if (t.isStackable())
		{
			ItemInstance it = inv.addItem(r.id, r.count);
			if (it != null)
				Log.LogItem(p, Log.ItemLog.Create, it, r.count);
		}
		else
		{
			boolean ench = false;
			for (long i = 0; i < r.count; i++)
			{
				ItemInstance it = inv.addItem(r.id, 1L);
				if (it != null)
				{
					if (r.enchant > 0)
					{
						it.setEnchantLevel(r.enchant);
						ench = true;
					}
					Log.LogItem(p, Log.ItemLog.Create, it);
				}
			}
			if (ench)
				p.sendItemList(false);
		}
		p.sendPacket((IStaticPacket) SystemMessage2.obtainItems(r.id, r.count, r.enchant));
	}

	private static boolean mail(Player p, Reward r, String room)
	{
		List<long[]> items = new ArrayList<>();
		items.add(new long[] { r.id, r.count, r.enchant });
		String body = room + "\nO item do Random Craft veio pelo correio: " + name(r.id) + " x" + fmt(r.count) + ".";
		List<long[]> rest = MailService.sendSystemMailRest(p.getObjectId(), RandomCraftConfig.MAIL_SENDER, "Item do Random Craft", body, items, true);
		return rest.isEmpty();
	}

	// ── extrair (Craft Points) ────────────────────────────────────────

	public static void list(Player p)
	{
		String why = check(p);
		RandomCraftData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Random Craft indisponível no momento. Tente de novo.";
		if (why != null)
		{
			fail(p, "list", why);
			return;
		}
		String pts;
		synchronized (d)
		{
			pts = d.points + "|" + d.exp + "|" + RandomCraftConfig.MAX_EXP + "|" + RandomCraftConfig.MAX_POINTS;
		}
		ui(p, "PTS", pts);
		sendList(p);
	}

	private static void sendList(Player p)
	{
		List<String> rows = new ArrayList<>();
		RandomCraftData d = get(p.getObjectId());
		long room = 0;
		if (d != null)
			synchronized (d)
			{
				room = roomLeft(d);
			}
		// 6º campo: 1 = ainda cabe experiência; 0 = barra no limite (item "apagado" na lista)
		for (Extract e : RandomCraftConfig.EXTRACT.values())
		{
			long n = extractable(e.id) ? have(p, e.id) : 0L;
			if (n > 0)
				rows.add(e.id + "," + n + ",0," + e.exp + "," + e.adena + "," + (room > 0 ? 1 : 0));
		}
		StringBuilder b = new StringBuilder().append(rows.size());
		for (String r : rows)
			b.append('|').append(r);
		ui(p, "LIST", b.toString());
	}

	/**
	 * Teto de experiência total (pontos * MaxExp + barra): MaxPoints pontos e a barra
	 * em 99,9% (MaxExp = 1000 -> 999). Só depois disso a extração é bloqueada.
	 */
	static long capExp()
	{
		long max = RandomCraftConfig.MAX_EXP;
		return (long) RandomCraftConfig.MAX_POINTS * max + Math.max(0L, max * 999L / 1000L);
	}

	/** Quanta experiência ainda cabe antes do teto (0 = bloqueado). */
	static long roomLeft(RandomCraftData d)
	{
		return Math.max(0L, capExp() - ((long) d.points * RandomCraftConfig.MAX_EXP + d.exp));
	}

	/** Conta feita para uma seleção "id:qtd id:qtd". */
	static final class Calc
	{
		String error;
		final Map<Integer, Long> take = new LinkedHashMap<>();
		long gain;
		long fee;
		int newPoints;
		long newExp;
		boolean over;
	}

	static Calc calc(Player p, RandomCraftData d, String[] a)
	{
		Calc c = new Calc();
		if (a != null)
			for (String s : a)
			{
				String[] f = s.split(":");
				if (f.length < 2)
					continue;
				int id;
				long n;
				try
				{
					id = Integer.parseInt(f[0].trim());
					n = Long.parseLong(f[1].trim());
				}
				catch (NumberFormatException e)
				{
					c.error = "Seleção inválida.";
					return c;
				}
				if (n <= 0)
					continue;
				if (!RandomCraftConfig.EXTRACT.containsKey(id) || !extractable(id))
				{
					c.error = "Esse item não pode ser extraído.";
					return c;
				}
				Long cur = c.take.get(id);
				c.take.put(id, (cur == null ? 0L : cur) + n);
			}
		for (Map.Entry<Integer, Long> e : c.take.entrySet())
		{
			Extract x = RandomCraftConfig.EXTRACT.get(e.getKey());
			if (have(p, x.id) < e.getValue())
			{
				c.error = "Você não tem " + fmt(e.getValue()) + " " + name(x.id) + ".";
				return c;
			}
			if (x.exp * e.getValue() > 4000000000000L || x.adena * e.getValue() > 4000000000000L)
			{
				c.error = "Quantidade grande demais.";
				return c;
			}
		}

		// Teto: o que passar do limite é cortado. Itens empilháveis entram só na
		// quantidade necessária para encostar no teto (a sobra de experiência é
		// descartada); sem espaço nenhum, o item sai da seleção.
		long room = roomLeft(d);
		Iterator<Map.Entry<Integer, Long>> it = c.take.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Long> e = it.next();
			Extract x = RandomCraftConfig.EXTRACT.get(e.getKey());
			long n = e.getValue();
			if (room <= 0)
			{
				it.remove();
				c.over = true;
				continue;
			}
			long exp = x.exp * n;
			if (exp > room)
			{
				n = (room + x.exp - 1) / x.exp; // arredonda para cima: encosta no teto
				e.setValue(n);
				exp = room;
				c.over = true;
			}
			room -= exp;
			c.gain += exp;
			c.fee += x.adena * n;
		}
		if (room <= 0 && !c.take.isEmpty())
			c.over = true;

		long total = (long) d.points * RandomCraftConfig.MAX_EXP + d.exp + c.gain;
		c.newPoints = (int) (total / RandomCraftConfig.MAX_EXP);
		c.newExp = total % RandomCraftConfig.MAX_EXP;
		return c;
	}

	/**
	 * Só item empilhável entra na extração: a retirada é por id (destroyItemByItemId), que num item não
	 * empilhável pegaria uma instância qualquer (podendo ser a equipada ou a encantada) e falharia com mais de 1.
	 */
	static boolean extractable(int id)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(id);
		return t != null && t.isStackable();
	}

	/** Seleção já cortada pelo teto ("id:qtd id:qtd"), para o cliente acertar a lista. */
	private static String takeString(Calc c)
	{
		StringBuilder b = new StringBuilder();
		for (Map.Entry<Integer, Long> e : c.take.entrySet())
		{
			if (b.length() > 0)
				b.append(' ');
			b.append(e.getKey()).append(':').append(e.getValue());
		}
		return b.toString();
	}

	public static void preview(Player p, String[] a)
	{
		String why = check(p);
		RandomCraftData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Random Craft indisponível no momento. Tente de novo.";
		if (why != null)
		{
			fail(p, "preview", why);
			return;
		}
		Calc c;
		int points;
		long exp;
		synchronized (d)
		{
			c = calc(p, d, a);
			points = d.points;
			exp = d.exp;
		}
		if (c.error != null)
		{
			fail(p, "preview", c.error);
			return;
		}
		ui(p, "PREV", c.gain + "|" + c.fee + "|" + c.newPoints + "|" + c.newExp + "|" + RandomCraftConfig.MAX_EXP + "|" + points + "|" + exp + "|" + (c.over ? 1 : 0) + "|" + takeString(c));
	}

	public static void process(Player p, String[] a)
	{
		if (!throttle(p))
			return;
		String why = check(p);
		RandomCraftData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Random Craft indisponível no momento. Tente de novo.";
		if (why != null)
		{
			fail(p, "process", why);
			return;
		}
		boolean done = false;
		long gain = 0;
		synchronized (d)
		{
			Calc c = calc(p, d, a);
			if (c.error != null)
				why = c.error;
			else if (c.take.isEmpty())
				why = c.over ? "Você já está no limite de Craft Points." : "Nenhum item selecionado.";
			else if (p.getAdena() < c.fee)
				why = "Você precisa de " + fmt(c.fee) + " de Adena.";
			else
			{
				boolean okAll = true;
				List<long[]> taken = new ArrayList<>();
				for (Map.Entry<Integer, Long> e : c.take.entrySet())
				{
					if (!p.getInventory().destroyItemByItemId(e.getKey(), e.getValue()))
					{
						okAll = false;
						break;
					}
					taken.add(new long[] { e.getKey(), e.getValue() });
					Log.LogItem(p, Log.ItemLog.Delete, e.getKey(), e.getValue());
					p.sendPacket((IStaticPacket) SystemMessage2.removeItems(e.getKey(), e.getValue()));
				}
				if (okAll && c.fee > 0 && !p.reduceAdena(c.fee, true))
				{
					okAll = false;
					why = "Não foi possível cobrar a Adena.";
				}
				if (!okAll)
				{
					// devolve o que já tinha saído (antes os itens ficavam perdidos sem virar pontos)
					for (long[] t : taken)
					{
						p.getInventory().addItem((int) t[0], t[1]);
						p.sendPacket((IStaticPacket) SystemMessage2.obtainItems((int) t[0], t[1], 0));
					}
					if (why == null)
						why = "Não foi possível tirar os itens. Tente de novo.";
				}
				else
				{
					d.points = c.newPoints;
					d.exp = c.newExp;
					gain = c.gain;
					done = true;
				}
			}
		}
		if (done)
		{
			save(d);
			ok(p, "process", "Você extraiu " + fmt(gain) + " de experiência de craft.");
			ui(p, "PTS", d.points + "|" + d.exp + "|" + RandomCraftConfig.MAX_EXP + "|" + RandomCraftConfig.MAX_POINTS);
			sendList(p);
		}
		else
			fail(p, "process", why);
	}

	// ── GM ────────────────────────────────────────────────────────────

	public static int gmAddPoints(Player p, int n)
	{
		RandomCraftData d = get(p.getObjectId());
		if (d == null)
			return -1;
		int now;
		synchronized (d)
		{
			d.points = Math.max(0, Math.min(RandomCraftConfig.MAX_POINTS, d.points + n));
			now = d.points;
		}
		save(d);
		return now;
	}

	public static void gmReset(Player p)
	{
		RandomCraftData d = get(p.getObjectId());
		if (d == null)
			return;
		synchronized (d)
		{
			d.points = 0;
			d.exp = 0;
			d.clearSlots();
		}
		save(d);
	}

	// para os testes
	static void clearForTests()
	{
		CACHE.clear();
		LAST_ACTION.clear();
	}

	static void resetThrottle()
	{
		LAST_ACTION.clear();
	}
}
