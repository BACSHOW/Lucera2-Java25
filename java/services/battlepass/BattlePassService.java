package services.battlepass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.LongSupplier;

import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.battlepass.BattlePassConfig.Step;
import services.auctionhouse.AuctionHouseService;
import services.mail.MailService;
import services.mail.MailText;

/**
 * Battle Pass (janela L2PassWnd do cliente).
 *
 * Pontos por matar monstros; cada degrau tem uma recompensa grátis e uma
 * premium (a premium só com o premium ativo, comprado com a moeda do
 * battlepass.properties). O progresso é por temporada: quando ela vira,
 * pontos, premium e resgates zeram.
 *
 * Conversa com a janela (como o correio):
 *   cliente  -> servidor: bypass "scripts_services.BattlePass:bp..."
 *   servidor -> cliente: TutorialShowHtml "PASS|tipo|dados" (o TutorialViewerWnd entrega à L2PassWnd):
 *     OPEN                       abre a janela
 *     PAGE   pagina|paginas|premium|pontos|maxPontos|nivel|fimTemporada|
 *            moedaPremium|qtdPremium|pontosDegrauAnterior|n|degrau|...|degrau
 *            degrau = pontos,idGratis,qtdGratis,tipoGratis,bodyGratis,pegouGratis,
 *                     idPremium,qtdPremium,tipoPremium,bodyPremium,pegouPremium
 *     PTS    pontos|nivel        pontos novos (só com a janela aberta)
 *     OK     op|texto            deu certo (o cliente mostra o texto)
 *     FAIL   op|texto            não deu (o cliente mostra em vermelho)
 */
public final class BattlePassService
{
	private static final Logger _log = LoggerFactory.getLogger(BattlePassService.class);

	public static final String CHANNEL = "PASS";
	private static final long DAY = 86400000L;
	private static final long ACTION_DELAY_MS = 300L;
	/** Itens por correio (o do sistema manda até 8 anexos de uma vez). */
	private static final int MAIL_UNITS = 8;

	static BattlePassStore STORE = new BattlePassStore.Jdbc();
	static LongSupplier CLOCK = System::currentTimeMillis;

	private static final Map<Integer, BattlePassData> CACHE = new ConcurrentHashMap<>();
	/** Jogadores com a janela aberta (recebem os pontos ao vivo). */
	private static final Set<Integer> OPEN = ConcurrentHashMap.newKeySet();
	private static final Map<Integer, Long> LAST_ACTION = new ConcurrentHashMap<>();
	private static ScheduledFuture<?> _saveTask;

	private BattlePassService()
	{
	}

	// =====================================================================
	// Temporada
	// =====================================================================

	static long seasonStart(long now)
	{
		long start = BattlePassConfig.SEASON_START;
		int days = BattlePassConfig.SEASON_DAYS;
		if (days <= 0 || now < start)
			return start;
		long len = days * DAY;
		return start + ((now - start) / len) * len;
	}

	/** 0 = sem fim (SeasonDays = 0). */
	static long seasonEnd(long now)
	{
		int days = BattlePassConfig.SEASON_DAYS;
		return days <= 0 ? 0L : seasonStart(now) + days * DAY;
	}

	/** Chave gravada no banco: o dia em que a temporada começou. */
	static int seasonKey(long now)
	{
		return (int) Math.floorDiv(seasonStart(now), DAY);
	}

	static String seasonText(long now)
	{
		long end = seasonEnd(now);
		if (end <= 0L)
			return "sem prazo";
		return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(end));
	}

	/** Temporada nova: zera tudo. true = zerou. */
	static boolean roll(BattlePassData d, long now)
	{
		int key = seasonKey(now);
		synchronized (d)
		{
			if (d.season == key)
				return false;
			d.season = key;
			d.points = 0;
			d.premium = false;
			d.freeClaimed = 0L;
			d.premClaimed = 0L;
			d.changed();
			return true;
		}
	}

	// =====================================================================
	// Dados
	// =====================================================================

	/** Progresso do personagem (carrega do banco na 1a vez). null = banco falhou. */
	public static BattlePassData get(int objId)
	{
		BattlePassData d = CACHE.get(objId);
		if (d == null)
		{
			BattlePassData loaded;
			try
			{
				loaded = STORE.load(objId);
			}
			catch (Exception e)
			{
				_log.error("BattlePass: erro lendo o progresso de objId " + objId + ": " + e);
				return null;
			}
			if (loaded == null)
			{
				loaded = new BattlePassData(objId);
				loaded.season = seasonKey(CLOCK.getAsLong());
			}
			BattlePassData prev = CACHE.putIfAbsent(objId, loaded);
			d = prev != null ? prev : loaded;
		}
		roll(d, CLOCK.getAsLong());
		return d;
	}

	static boolean save(BattlePassData d)
	{
		int v;
		synchronized (d)
		{
			v = d.version;
		}
		try
		{
			STORE.save(d);
		}
		catch (Exception e)
		{
			_log.error("BattlePass: erro gravando o progresso de objId " + d.objId + ": " + e);
			return false;
		}
		synchronized (d)
		{
			if (d.savedVersion < v)
				d.savedVersion = v;
		}
		return true;
	}

	/** Grava os pontos pendentes; tira do cache quem já saiu. */
	public static void saveAll()
	{
		for (BattlePassData d : CACHE.values())
		{
			boolean dirty;
			synchronized (d)
			{
				dirty = d.version != d.savedVersion;
			}
			if (dirty)
				save(d);
			Player p = World.getPlayer(d.objId);
			if (p == null || !p.isOnline())
			{
				synchronized (d)
				{
					if (d.version == d.savedVersion)
						CACHE.remove(d.objId, d);
				}
			}
		}
	}

	public static void startSaveTask()
	{
		stopSaveTask();
		long ms = BattlePassConfig.SAVE_INTERVAL * 1000L;
		_saveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(BattlePassService::saveAllSafe, ms, ms);
	}

	public static void stopSaveTask()
	{
		if (_saveTask != null)
		{
			_saveTask.cancel(false);
			_saveTask = null;
		}
	}

	private static void saveAllSafe()
	{
		try
		{
			saveAll();
		}
		catch (Exception e)
		{
			_log.error("BattlePass: erro na gravação periódica: " + e);
		}
	}

	/** Saiu do jogo: grava e tira do cache. */
	public static void onExit(Player p)
	{
		int id = p.getObjectId();
		OPEN.remove(id);
		LAST_ACTION.remove(id);
		BattlePassData d = CACHE.get(id);
		if (d == null)
			return;
		boolean dirty;
		synchronized (d)
		{
			dirty = d.version != d.savedVersion;
		}
		if (!dirty || save(d))
			CACHE.remove(id, d);
	}

	// =====================================================================
	// Pontos
	// =====================================================================

	/** Pontos de uma morte para um jogador deste nível. */
	public static int killPoints(int playerLevel, int mobLevel, boolean raid, boolean boss, int champion)
	{
		if (!BattlePassConfig.ENABLED || playerLevel < BattlePassConfig.MIN_LEVEL)
			return 0;
		if (BattlePassConfig.MAX_LEVEL_DIFF > 0 && mobLevel < playerLevel - BattlePassConfig.MAX_LEVEL_DIFF)
			return 0;
		if (boss)
			return BattlePassConfig.POINTS_BOSS;
		if (raid)
			return BattlePassConfig.POINTS_RAID;
		if (champion > 0)
			return BattlePassConfig.POINTS_CHAMPION;
		return BattlePassConfig.POINTS_MONSTER;
	}

	/** Degraus completos com estes pontos. */
	public static int level(int points)
	{
		int n = 0;
		for (Step s : BattlePassConfig.STEPS)
			if (points >= s.need)
				n++;
		return n;
	}

	public static void addPoints(Player p, int pts)
	{
		if (p == null || pts <= 0 || !BattlePassConfig.ENABLED)
			return;
		BattlePassData d = get(p.getObjectId());
		if (d == null)
			return;
		int max = BattlePassConfig.maxPoints();
		int before, after;
		synchronized (d)
		{
			before = d.points;
			if (before >= max)
				return;
			after = (int) Math.min((long) max, (long) before + pts);
			d.points = after;
			d.changed();
		}
		int lvBefore = level(before);
		int lvAfter = level(after);
		if (lvAfter > lvBefore && BattlePassConfig.STEP_NOTIFY)
			screen(p, lvAfter >= BattlePassConfig.STEPS.size()
					? "Battle Pass: todos os degraus completos! Use .pass para pegar as recompensas."
					: "Battle Pass: degrau " + lvAfter + " completo! Use .pass para pegar a recompensa.");
		if (OPEN.contains(p.getObjectId()))
			ui(p, "PTS", after + "|" + lvAfter);
	}

	// =====================================================================
	// Janela
	// =====================================================================

	public static void ui(Player p, String type, String data)
	{
		if (p != null)
			p.sendPacket(new TutorialShowHtml(CHANNEL + "|" + type + "|" + (data == null ? "" : data)));
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

	public static void open(Player p)
	{
		if (!BattlePassConfig.ENABLED)
		{
			p.sendMessage("O Battle Pass está desligado.");
			return;
		}
		ui(p, "OPEN", "");
	}

	public static void close(Player p)
	{
		OPEN.remove(p.getObjectId());
	}

	public static void page(Player p, int page)
	{
		if (!BattlePassConfig.ENABLED)
		{
			fail(p, "page", "O Battle Pass está desligado.");
			return;
		}
		BattlePassData d = get(p.getObjectId());
		if (d == null)
		{
			fail(p, "page", "Battle Pass indisponível no momento. Tente de novo.");
			return;
		}
		OPEN.add(p.getObjectId());
		sendPage(p, d, page);
	}

	static int pages()
	{
		int n = BattlePassConfig.STEPS.size();
		return Math.max(1, (n + BattlePassConfig.PAGE_SIZE - 1) / BattlePassConfig.PAGE_SIZE);
	}

	static void sendPage(Player p, BattlePassData d, int page)
	{
		List<Step> steps = BattlePassConfig.STEPS;
		int pages = pages();
		if (page < 1)
			page = 1;
		if (page > pages)
			page = pages;
		int points;
		boolean premium;
		long free, prem;
		synchronized (d)
		{
			points = d.points;
			premium = d.premium;
			free = d.freeClaimed;
			prem = d.premClaimed;
		}
		int from = (page - 1) * BattlePassConfig.PAGE_SIZE;
		int to = Math.min(steps.size(), from + BattlePassConfig.PAGE_SIZE);
		int prevNeed = from > 0 ? steps.get(from - 1).need : 0;

		StringBuilder sb = new StringBuilder(1024);
		sb.append(page).append('|').append(pages)
				.append('|').append(premium ? 1 : 0)
				.append('|').append(points)
				.append('|').append(BattlePassConfig.maxPoints())
				.append('|').append(level(points))
				.append('|').append(MailText.out(seasonText(CLOCK.getAsLong())))
				.append('|').append(BattlePassConfig.PREMIUM_ITEM_ID)
				.append('|').append(BattlePassConfig.PREMIUM_ITEM_COUNT)
				.append('|').append(prevNeed)
				.append('|').append(to - from);
		for (int i = from; i < to; i++)
		{
			Step s = steps.get(i);
			long bit = 1L << i;
			sb.append('|').append(s.need);
			reward(sb, s.freeId, s.freeCount, (free & bit) != 0);
			reward(sb, s.premId, s.premCount, (prem & bit) != 0);
		}
		ui(p, "PAGE", sb.toString());
	}

	private static void reward(StringBuilder sb, int id, long count, boolean taken)
	{
		ItemTemplate t = id > 0 ? ItemHolder.getInstance().getTemplate(id) : null;
		sb.append(',').append(t != null ? id : 0)
				.append(',').append(t != null ? count : 0)
				.append(',').append(t != null ? AuctionHouseService.itemType(t) : 0)
				.append(',').append(t != null ? t.getBodyPart() : 0)
				.append(',').append(taken ? 1 : 0);
	}

	// =====================================================================
	// Resgate
	// =====================================================================

	private static boolean throttle(Player p)
	{
		long t = System.currentTimeMillis();
		Long last = LAST_ACTION.put(p.getObjectId(), t);
		return last == null || t - last >= ACTION_DELAY_MS;
	}

	/** Situações em que não se mexe no inventário. null = pode. */
	static String busy(Player p)
	{
		if (p.isInStoreMode())
			return "Feche a loja particular primeiro.";
		if (p.isInTrade())
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

	/** Uma recompensa a entregar. */
	static final class Reward
	{
		final int step;        // 1..N
		final boolean premium;
		final int id;
		final long count;

		Reward(int step, boolean premium, int id, long count)
		{
			this.step = step;
			this.premium = premium;
			this.id = id;
			this.count = count;
		}

		int units()
		{
			ItemTemplate t = ItemHolder.getInstance().getTemplate(id);
			return t == null || t.isStackable() ? 1 : (int) Math.min(count, 1000L);
		}

		String label()
		{
			return "degrau " + step + (premium ? " (premium)" : "");
		}
	}

	/** Cabe no inventário agora? null = cabe. */
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
				Log.LogItem(p, Log.ItemLog.AchievementReceived, it, r.count);
		}
		else
			for (long i = 0; i < r.count; i++)
			{
				ItemInstance it = inv.addItem(r.id, 1L);
				if (it != null)
					Log.LogItem(p, Log.ItemLog.AchievementReceived, it);
			}
		p.sendPacket((IStaticPacket) SystemMessage2.obtainItems(r.id, r.count, 0));
	}

	/** Correio com estas recompensas (até 8 unidades: uma mensagem só, tudo ou nada). */
	private static boolean mail(Player p, List<Reward> group)
	{
		List<long[]> items = new ArrayList<>();
		StringBuilder what = new StringBuilder();
		for (Reward r : group)
		{
			items.add(new long[] { r.id, r.count, 0L });
			if (what.length() > 0)
				what.append(", ");
			what.append(r.label());
		}
		String topic = group.size() == 1 ? "Recompensa do " + group.get(0).label() : "Recompensas do Battle Pass";
		String body = "Seu inventário estava sem espaço, então a recompensa veio pelo correio.\n" + what + ".";
		List<long[]> rest = MailService.sendSystemMailRest(p.getObjectId(), BattlePassConfig.MAIL_SENDER, topic, body, items, true);
		return rest.isEmpty();
	}

	private static void mark(BattlePassData d, Reward r)
	{
		long bit = 1L << (r.step - 1);
		if (r.premium)
			d.premClaimed |= bit;
		else
			d.freeClaimed |= bit;
		d.changed();
	}

	private static void unmark(BattlePassData d, Reward r)
	{
		long bit = 1L << (r.step - 1);
		if (r.premium)
			d.premClaimed &= ~bit;
		else
			d.freeClaimed &= ~bit;
		d.changed();
	}

	/** Pega a recompensa de um degrau (premium = trilha premium). */
	public static void claim(Player p, int step, boolean premium, int page)
	{
		if (!throttle(p))
			return;
		String why = check(p);
		BattlePassData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Battle Pass indisponível no momento. Tente de novo.";
		if (why != null)
		{
			fail(p, "claim", why);
			return;
		}
		List<Step> steps = BattlePassConfig.STEPS;
		if (step < 1 || step > steps.size())
		{
			fail(p, "claim", "Degrau inválido.");
			return;
		}
		Step s = steps.get(step - 1);
		int id = premium ? s.premId : s.freeId;
		long count = premium ? s.premCount : s.freeCount;
		String done = null;
		boolean byMail = false;
		synchronized (d)
		{
			if (id <= 0)
				why = "Esse degrau não tem recompensa " + (premium ? "premium." : "grátis.");
			else if (d.points < s.need)
				why = "Faltam " + (s.need - d.points) + " pontos para o degrau " + step + ".";
			else if (premium && !d.premium)
				why = "Ative o Premium para pegar as recompensas premium.";
			else if (premium ? d.premTaken(step) : d.freeTaken(step))
				why = "Você já pegou essa recompensa.";
			else
			{
				Reward r = new Reward(step, premium, id, count);
				String room = fit(p.getInventory(), id, count);
				if (room == null)
				{
					mark(d, r);
					give(p, r);
					done = "Recompensa do " + r.label() + " recebida.";
				}
				else if (BattlePassConfig.MAIL_WHEN_FULL)
				{
					mark(d, r);
					List<Reward> one = new ArrayList<>();
					one.add(r);
					if (mail(p, one))
					{
						byMail = true;
						done = room + " A recompensa do " + r.label() + " foi pelo correio.";
					}
					else
					{
						unmark(d, r);
						why = "Não foi possível entregar agora. Tente de novo.";
					}
				}
				else
					why = room;
			}
		}
		if (done != null)
		{
			save(d);
			ok(p, byMail ? "mail" : "claim", done);
		}
		else
			fail(p, "claim", why);
		sendPage(p, d, page);
	}

	/** Pega tudo o que já dá (grátis e, com premium, as premium). */
	public static void claimAll(Player p, int page)
	{
		if (!throttle(p))
			return;
		String why = check(p);
		BattlePassData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Battle Pass indisponível no momento. Tente de novo.";
		if (why != null)
		{
			fail(p, "all", why);
			return;
		}
		List<Step> steps = BattlePassConfig.STEPS;
		int got = 0, mailed = 0;
		String full = null;
		boolean mailFail = false;
		synchronized (d)
		{
			List<Reward> list = new ArrayList<>();
			for (int i = 0; i < steps.size(); i++)
			{
				Step s = steps.get(i);
				if (d.points < s.need)
					break;
				if (s.freeId > 0 && !d.freeTaken(i + 1))
					list.add(new Reward(i + 1, false, s.freeId, s.freeCount));
				if (d.premium && s.premId > 0 && !d.premTaken(i + 1))
					list.add(new Reward(i + 1, true, s.premId, s.premCount));
			}
			if (list.isEmpty())
			{
				fail(p, "all", d.points < (steps.isEmpty() ? 0 : steps.get(0).need)
						? "Nenhum degrau completo ainda."
						: "Nada para pegar agora.");
				sendPage(p, d, page);
				return;
			}
			List<Reward> toMail = new ArrayList<>();
			for (Reward r : list)
			{
				String room = fit(p.getInventory(), r.id, r.count);
				if (room == null)
				{
					mark(d, r);
					give(p, r);
					got++;
				}
				else
				{
					if (full == null)
						full = room;
					if (BattlePassConfig.MAIL_WHEN_FULL)
						toMail.add(r);
				}
			}
			// correio em grupos de até 8 unidades: cada grupo é uma mensagem (tudo ou nada)
			List<Reward> group = new ArrayList<>();
			int units = 0;
			for (int k = 0; k <= toMail.size(); k++)
			{
				Reward r = k < toMail.size() ? toMail.get(k) : null;
				if (r != null && (group.isEmpty() || units + r.units() <= MAIL_UNITS))
				{
					group.add(r);
					units += r.units();
					continue;
				}
				if (!group.isEmpty())
				{
					for (Reward g : group)
						mark(d, g);
					if (mail(p, group))
						mailed += group.size();
					else
					{
						for (Reward g : group)
							unmark(d, g);
						mailFail = true;
					}
				}
				group = new ArrayList<>();
				units = 0;
				if (r != null)
				{
					group.add(r);
					units = r.units();
				}
			}
		}
		if (got > 0 || mailed > 0)
			save(d);
		if (got > 0 || mailed > 0)
		{
			StringBuilder t = new StringBuilder();
			if (got > 0)
				t.append(got == 1 ? "1 recompensa recebida." : got + " recompensas recebidas.");
			if (mailed > 0)
			{
				if (t.length() > 0)
					t.append(' ');
				t.append(full != null ? full + " " : "").append(mailed == 1 ? "1 foi pelo correio." : mailed + " foram pelo correio.");
			}
			ok(p, mailed > 0 ? "mail" : "all", t.toString());
		}
		if (mailFail)
			fail(p, "all", "Parte não pôde ser entregue agora. Tente de novo.");
		else if (got == 0 && mailed == 0 && full != null)
			fail(p, "all", full);
		else if (full != null && !BattlePassConfig.MAIL_WHEN_FULL)
			fail(p, "all", full + " O resto fica para depois.");
		sendPage(p, d, page);
	}

	/** Ativa o premium pagando a moeda configurada. */
	public static void buyPremium(Player p, int page)
	{
		if (!throttle(p))
			return;
		String why = check(p);
		BattlePassData d = why == null ? get(p.getObjectId()) : null;
		if (why == null && d == null)
			why = "Battle Pass indisponível no momento. Tente de novo.";
		if (why == null && BattlePassConfig.PREMIUM_ITEM_ID <= 0)
			why = "O Premium não está à venda.";
		if (why != null)
		{
			fail(p, "premium", why);
			return;
		}
		int id = BattlePassConfig.PREMIUM_ITEM_ID;
		long count = BattlePassConfig.PREMIUM_ITEM_COUNT;
		ItemTemplate t = ItemHolder.getInstance().getTemplate(id);
		String name = t != null ? t.getName() : ("item " + id);
		boolean done = false;
		synchronized (d)
		{
			if (d.premium)
				why = "O Premium já está ativo nesta temporada.";
			else if (p.getInventory().getCountOf(id) < count)
				why = "Você precisa de " + AuctionHouseService.fmt(count) + " " + name + " para ativar o Premium.";
			else if (!p.getInventory().destroyItemByItemId(id, count))
				why = "Não foi possível cobrar o Premium. Tente de novo.";
			else
			{
				Log.LogItem(p, Log.ItemLog.Delete, id, count);
				d.premium = true;
				d.changed();
				done = true;
			}
		}
		if (done)
		{
			save(d);
			p.sendPacket((IStaticPacket) SystemMessage2.removeItems(id, count));
			screen(p, "Battle Pass Premium ativado!");
			ok(p, "premium", "Premium ativado até o fim da temporada.");
		}
		else
			fail(p, "premium", why);
		sendPage(p, d, page);
	}

	private static String check(Player p)
	{
		if (!BattlePassConfig.ENABLED)
			return "O Battle Pass está desligado.";
		return busy(p);
	}

	// =====================================================================
	// GM
	// =====================================================================

	/** .passpoints: soma pontos (para testar). */
	public static int gmAddPoints(Player p, int pts)
	{
		addPoints(p, pts);
		BattlePassData d = get(p.getObjectId());
		if (d == null)
			return -1;
		synchronized (d)
		{
			return d.points;
		}
	}

	/** .passreset: zera o próprio progresso (para testar). */
	public static void gmReset(Player p)
	{
		BattlePassData d = get(p.getObjectId());
		if (d == null)
			return;
		synchronized (d)
		{
			d.points = 0;
			d.premium = false;
			d.freeClaimed = 0L;
			d.premClaimed = 0L;
			d.changed();
		}
		save(d);
	}

	// para os testes
	static void clearForTests()
	{
		CACHE.clear();
		OPEN.clear();
		LAST_ACTION.clear();
	}

	static void resetThrottle()
	{
		LAST_ACTION.clear();
	}

	static boolean isOpen(int objId)
	{
		return OPEN.contains(objId);
	}

	static BattlePassData cached(int objId)
	{
		return CACHE.get(objId);
	}
}
