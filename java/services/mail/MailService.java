package services.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import l2.commons.dao.JdbcEntityState;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.dao.CharacterPostFriendDAO;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.database.mysql;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExMailArrived;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

import org.napile.primitive.maps.IntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.auctionhouse.AuctionHouseService;

/**
 * Correio (janela MailWnd do cliente).
 *
 * Usa o motor de correio que o núcleo já tem (Mail, MailDAO, tabelas mail /
 * mail_attachments / character_mail, itens em location MAIL e a expiração do
 * DeleteExpiredMailTask). Os pacotes de Gracia (RequestEx*Post) não existem no
 * cliente Interlude. A conversa com a janela:
 *   cliente  -> servidor: bypass "scripts_services.MailBox:..." (pacote
 *               RequestBypassToServer);
 *   servidor -> cliente: TutorialShowHtml "MAIL|tipo|dados" (tipo = BOX,
 *               ROW_n, READ...); o TutorialViewerWnd entrega à MailWnd. Nada
 *               passa pelo chat.
 *
 * Toda operação que mexe em correio roda dentro de synchronized(lock()), a
 * mesma trava que a expiração do núcleo usa: retirar, devolver, cancelar e
 * expirar nunca acontecem ao mesmo tempo no mesmo correio.
 */
public final class MailService
{
	private static final Logger _log = LoggerFactory.getLogger(MailService.class);

	/** Canal do correio: prefixo do TutorialShowHtml ("MAIL|tipo|dados"), que o TutorialViewerWnd entrega à MailWnd. */
	public static final String CHANNEL = "MAIL";

	/** Texto do correio aberto vai em partes deste tamanho (BODY). */
	private static final int BODY_PART = 1000;
	/** objectIds por linha INV. */
	private static final int INV_PER_LINE = 100;

	public static final String RETURNED_TAG = "[Devolvido] ";
	private static final int ADENA = 57;

	// flags da linha e do correio aberto
	public static final int F_UNREAD = 1;          // recebido: não lido; enviado: o destinatário não leu
	public static final int F_ATT = 2;             // tem itens ou Adena anexados
	public static final int F_COD = 4;             // com cobrança
	public static final int F_SYSTEM = 8;          // do sistema (leilão, devolvido): não se devolve nem responde
	public static final int F_RETURNED = 16;       // devolvido
	public static final int F_TRANSIT = 64;        // enviado: ainda em entrega

	// ações permitidas no correio aberto (o cliente só mostra os botões)
	public static final int A_RECEIVE = 1;
	public static final int A_PAY = 2;
	public static final int A_RETURN = 4;
	public static final int A_REPLY = 8;
	public static final int A_CANCEL = 16;
	public static final int A_DELETE = 32;

	/** Correio em entrega: messageId -> hora (ms) em que aparece para o destinatário. */
	private static final Map<Integer, Long> PENDING = new ConcurrentHashMap<>();
	/** Último envio de cada personagem (MailSendCooldown). */
	private static final Map<Integer, Long> LAST_SEND = new ConcurrentHashMap<>();
	/** Última ação de cada personagem (contra clique repetido). */
	private static final Map<Integer, Long> LAST_ACTION = new ConcurrentHashMap<>();
	private static final long ACTION_DELAY_MS = 250L;

	private MailService()
	{
	}

	/** A trava do correio: a mesma da expiração do núcleo (DeleteExpiredMailTask). */
	public static Object lock()
	{
		return MailDAO.getInstance();
	}

	private static int now()
	{
		return (int) (System.currentTimeMillis() / 1000L);
	}

	// =====================================================================
	// Respostas
	// =====================================================================

	/** Manda uma mensagem do correio para a janela MailWnd (pacote, não chat). */
	public static void ui(Player p, String type, String data)
	{
		if (p != null)
			p.sendPacket(new TutorialShowHtml(CHANNEL + "|" + type + "|" + (data == null ? "" : data)));
	}

	private static void ok(Player p, String op, String extra)
	{
		ui(p, "OK", op + "|" + extra);
	}

	private static void fail(Player p, String op, int id, String reason)
	{
		ui(p, "FAIL", op + "|" + id + "|" + MailText.out(reason));
	}

	private static boolean throttle(Player p)
	{
		long t = System.currentTimeMillis();
		Long last = LAST_ACTION.put(p.getObjectId(), t);
		return last == null || t - last >= ACTION_DELAY_MS;
	}

	/** Situações em que o correio não mexe em itens. null = pode. */
	private static String busy(Player p)
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

	/** Zona de paz pela configuração. withItems = a ação mexe com itens ou Adena. */
	private static String peace(Player p, boolean withItems)
	{
		if (p.isInPeaceZone())
			return null;
		if (MailConfig.PEACE_ZONE)
			return "O correio só funciona em zona de paz.";
		if (withItems && MailConfig.PEACE_ZONE_ITEMS)
			return "Itens e Adena pelo correio só em zona de paz.";
		return null;
	}

	/**
	 * Os itens cabem no inventário agora (peso, espaços e pilha até o limite
	 * de 2.147.483.647)? null = cabem. Pode lançar ArithmeticException.
	 */
	private static String fitProblem(PcInventory inv, Collection<ItemInstance> items)
	{
		long weight = 0L;
		int slots = 0;
		Map<Integer, Long> stacks = new HashMap<>();
		for (ItemInstance it : items)
		{
			weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(it.getCount(), (long) it.getTemplate().getWeight()));
			if (!it.isStackable())
			{
				slots++;
				continue;
			}
			Long sum = stacks.get(it.getItemId());
			if (sum == null)
			{
				ItemInstance have = inv.getItemByItemId(it.getItemId());
				if (have == null)
					slots++;
				sum = have == null ? 0L : have.getCount();
			}
			sum = SafeMath.addAndCheck(sum, it.getCount());
			if (sum > Integer.MAX_VALUE)
				return "Você já tem o máximo de " + it.getName() + " que dá para carregar.";
			stacks.put(it.getItemId(), sum);
		}
		if (!inv.validateWeight(weight))
			return "Peso demais: libere peso no inventário.";
		if (!inv.validateCapacity(slots))
			return "Inventário cheio: libere " + slots + (slots == 1 ? " espaço." : " espaços.");
		return null;
	}

	/** O banco gravou o correio por inteiro? */
	private static boolean saved(Mail m)
	{
		return m.getMessageId() > 0 && m.getJdbcState() == JdbcEntityState.STORED;
	}

	/** Gravação que falhou no meio: apaga a linha (o banco apaga anexos e caixas junto). */
	private static void dropPartial(Mail m)
	{
		if (m == null || m.getMessageId() <= 0)
			return;
		try
		{
			m.getAttachments().clear();
			m.setJdbcState(JdbcEntityState.STORED);
			m.delete();
		}
		catch (Exception e)
		{
			_log.error("Mail: ERRO apagando o correio " + m.getMessageId() + " gravado pela metade — conferir no banco.", e);
		}
	}

	// =====================================================================
	// Entrega com atraso
	// =====================================================================

	public static boolean isPending(int messageId)
	{
		Long t = PENDING.get(messageId);
		if (t == null)
			return false;
		if (System.currentTimeMillis() >= t)
		{
			PENDING.remove(messageId);
			return false;
		}
		return true;
	}

	private static int pendingSeconds(int messageId)
	{
		Long t = PENDING.get(messageId);
		if (t == null)
			return 0;
		long ms = t - System.currentTimeMillis();
		return ms <= 0 ? 0 : (int) ((ms + 999L) / 1000L);
	}

	private static void deliver(final Mail m)
	{
		final int id = m.getMessageId();
		final int delay = MailConfig.DELIVERY_DELAY;
		if (delay <= 0)
		{
			arrived(id);
			return;
		}
		PENDING.put(id, System.currentTimeMillis() + delay * 1000L);
		ThreadPoolManager.getInstance().schedule(() -> arrived(id), delay * 1000L + 200L);
	}

	private static void arrived(int id)
	{
		PENDING.remove(id);
		try
		{
			Mail m = MailDAO.getInstance().load(id);
			if (m == null)
				return;   // cancelado durante a entrega
			Player r = World.getPlayer(m.getReceiverId());
			if (r != null && r.isOnline())
				notifyNew(r, m.getSenderName());
		}
		catch (Exception e)
		{
			_log.warn("Mail: aviso de entrega falhou (" + id + "): " + e.getMessage());
		}
	}

	/** Avisa o destinatário online: envelope acima do chat + janela (se aberta). */
	public static void notifyNew(Player r, String from)
	{
		try
		{
			r.sendPacket(ExMailArrived.STATIC);
			r.sendPacket(Msg.YOUVE_GOT_MAIL);
			ui(r, "NEW", unreadCount(r.getObjectId()) + "|" + MailText.out(from));
		}
		catch (Exception e)
		{
			// aviso é só enfeite
		}
	}

	// =====================================================================
	// Caixa (listas)
	// =====================================================================

	private static List<Mail> boxList(int objId, int tab)
	{
		List<Mail> src = tab == 0 ? MailDAO.getInstance().getReceivedMailByOwnerId(objId)
				: MailDAO.getInstance().getSentMailByOwnerId(objId);
		List<Mail> list = new ArrayList<>(src.size());
		for (Mail m : src)
		{
			if (tab == 0)
			{
				if (m.getReceiverId() != objId || isPending(m.getMessageId()))
					continue;
			}
			else if (m.getSenderId() != objId)
				continue;
			list.add(m);
		}
		Collections.sort(list);   // Mail.compareTo: o mais novo primeiro
		return list;
	}

	public static int unreadCount(int objId)
	{
		int n = 0;
		for (Mail m : boxList(objId, 0))
			if (m.isUnread())
				n++;
		return n;
	}

	private static int receivedCount(int objId)
	{
		return mysql.simple_get_int("COUNT(*)", "character_mail", "char_id=" + objId + " AND is_sender=0");
	}

	private static int flags(Mail m, int tab)
	{
		int f = 0;
		if (m.isUnread())
			f |= F_UNREAD;
		if (!m.getAttachments().isEmpty())
			f |= F_ATT;
		if (m.getPrice() > 0)
			f |= F_COD;
		if (m.getType() != Mail.SenderType.NORMAL)
			f |= F_SYSTEM;
		if (m.getTopic() != null && m.getTopic().startsWith(RETURNED_TAG))
			f |= F_RETURNED;
		if (tab == 1 && isPending(m.getMessageId()))
			f |= F_TRANSIT;
		return f;
	}

	private static int secondsLeft(Mail m, int tab)
	{
		if (tab == 1 && isPending(m.getMessageId()))
			return pendingSeconds(m.getMessageId());
		return Math.max(0, m.getExpireTime() - now());
	}

	/** BOX + ROW_n + END. */
	public static void sendBox(Player p, int tab, int page)
	{
		if (tab != 1)
			tab = 0;
		List<Mail> list;
		int unread;
		synchronized (lock())
		{
			list = boxList(p.getObjectId(), tab);
			unread = tab == 0 ? 0 : unreadCount(p.getObjectId());
			if (tab == 0)
				for (Mail m : list)
					if (m.isUnread())
						unread++;
		}
		int size = MailConfig.PAGE_SIZE;
		int pages = Math.max(1, (list.size() + size - 1) / size);
		if (page < 1)
			page = 1;
		if (page > pages)
			page = pages;

		ui(p, "BOX", tab + "|" + page + "|" + pages + "|" + unread + "|" + list.size() + "|" + MailConfig.INBOX_LIMIT);
		int from = (page - 1) * size;
		int n = 0;
		for (int i = from; i < list.size() && n < size; i++, n++)
		{
			Mail m = list.get(i);
			String name = tab == 0 ? m.getSenderName() : m.getReceiverName();
			ui(p, "ROW_" + n, m.getMessageId()
					+ "|" + flags(m, tab)
					+ "|" + MailText.out(name)
					+ "|" + MailText.out(m.getTopic())
					+ "|" + secondsLeft(m, tab)
					+ "|" + m.getAttachments().size()
					+ "|" + m.getPrice());
		}
		ui(p, "END", String.valueOf(n));
	}

	// =====================================================================
	// Ler
	// =====================================================================

	private static Mail find(Player p, int tab, int id)
	{
		if (id <= 0)
			return null;
		int objId = p.getObjectId();
		Mail m = tab == 0 ? MailDAO.getInstance().getReceivedMailByMailId(objId, id)
				: MailDAO.getInstance().getSentMailByMailId(objId, id);
		if (m == null)
			return null;
		if (tab == 0 && (m.getReceiverId() != objId || isPending(id)))
			return null;
		if (tab == 1 && m.getSenderId() != objId)
			return null;
		return m;
	}

	private static int actions(Mail m, int tab)
	{
		boolean att = !m.getAttachments().isEmpty();
		boolean normal = m.getType() == Mail.SenderType.NORMAL;
		if (tab == 0)
		{
			if (att)
			{
				if (!normal)
					return A_RECEIVE;
				return (m.getPrice() > 0 ? A_PAY : A_RECEIVE) | A_RETURN | A_REPLY;
			}
			return A_DELETE | (normal ? A_REPLY : 0);
		}
		// remetente: cancelar só enquanto o destinatário não abriu (com ou sem
		// itens); excluir só sem itens
		if (att)
			return m.isUnread() ? A_CANCEL : 0;
		return m.isUnread() ? (A_CANCEL | A_DELETE) : A_DELETE;
	}

	private static List<ItemInstance> sortedAttachments(Mail m)
	{
		List<ItemInstance> list = new ArrayList<>(m.getAttachments());
		list.sort(Comparator.comparingInt(ItemInstance::getObjectId));
		return list;
	}

	/** READ + BODY + ATT + READ_END. */
	private static void sendRead(Player p, int tab, Mail m)
	{
		// a Adena aparece nos anexos como qualquer item (o campo adena do READ
		// continua com o total, só para referência)
		List<ItemInstance> others = sortedAttachments(m);
		long adena = 0L;
		for (ItemInstance it : others)
			if (it.getItemId() == ADENA)
				adena += it.getCount();
		String body = m.getBody() == null ? "" : m.getBody();
		List<String> parts = new ArrayList<>();
		for (int i = 0; i < body.length(); i += BODY_PART)
			parts.add(body.substring(i, Math.min(body.length(), i + BODY_PART)));

		int id = m.getMessageId();
		ui(p, "READ", id
				+ "|" + tab
				+ "|" + flags(m, tab)
				+ "|" + MailText.out(m.getSenderName())
				+ "|" + MailText.out(m.getReceiverName())
				+ "|" + MailText.out(m.getTopic())
				+ "|" + secondsLeft(m, tab)
				+ "|" + m.getPrice()
				+ "|" + others.size()
				+ "|" + actions(m, tab)
				+ "|" + adena
				+ "|" + parts.size());
		for (int i = 0; i < parts.size(); i++)
			ui(p, "BODY", id + "|" + i + "|" + MailText.out(parts.get(i)));
		for (ItemInstance it : others)
		{
			ItemTemplate t = it.getTemplate();
			ui(p, "ATT", id
					+ "|" + it.getObjectId()
					+ "|" + it.getItemId()
					+ "|" + it.getCount()
					+ "|" + it.getEnchantLevel()
					+ "|" + AuctionHouseService.itemType(t)
					+ "|" + (t != null ? t.getBodyPart() : 0));
		}
		ui(p, "READ_END", String.valueOf(id));
	}

	public static void read(Player p, int tab, int id)
	{
		if (tab != 1)
			tab = 0;
		synchronized (lock())
		{
			Mail m = find(p, tab, id);
			if (m == null)
			{
				fail(p, "read", id, "Este correio não existe mais.");
				sendBox(p, tab, 1);
				return;
			}
			if (tab == 0 && m.isUnread())
			{
				m.setUnread(false);
				m.setJdbcState(JdbcEntityState.UPDATED);
				m.update();
			}
			sendRead(p, tab, m);
		}
	}

	// =====================================================================
	// Receber / Pagar
	// =====================================================================

	public static void receive(Player p, int id, boolean pay, long seenPrice)
	{
		final String op = pay ? "pay" : "receive";
		if (!throttle(p))
			return;
		String why = busy(p);
		if (why == null)
			why = peace(p, true);
		if (why != null)
		{
			fail(p, op, id, why);
			return;
		}

		long paid = 0L;
		Mail m;
		synchronized (lock())
		{
			m = find(p, 0, id);
			if (m == null)
			{
				fail(p, op, id, "Este correio não existe mais.");
				sendBox(p, 0, 1);
				return;
			}
			Set<ItemInstance> set = m.getAttachments();
			if (set.isEmpty())
			{
				fail(p, op, id, "Os itens deste correio já foram retirados.");
				sendRead(p, 0, m);
				return;
			}
			boolean cod = m.getPrice() > 0;
			if (cod && !pay)
			{
				fail(p, op, id, "Este correio tem cobrança: use Pagar.");
				sendRead(p, 0, m);
				return;
			}
			if (!cod && pay)
			{
				fail(p, op, id, "Este correio não tem cobrança.");
				sendRead(p, 0, m);
				return;
			}
			if (pay && seenPrice != m.getPrice())
			{
				fail(p, op, id, "O valor da cobrança não confere. Abra o correio de novo.");
				sendRead(p, 0, m);
				return;
			}

			PcInventory inv = p.getInventory();
			inv.writeLock();
			try
			{
				String full = fitProblem(inv, set);
				if (full != null)
				{
					fail(p, op, id, full);
					return;
				}
				if (pay)
				{
					if (p.getAdena() < m.getPrice() || !p.reduceAdena(m.getPrice(), true))
					{
						fail(p, op, id, "Adena insuficiente para pagar " + AuctionHouseService.fmt(m.getPrice()) + ".");
						return;
					}
					paid = m.getPrice();
				}

				ItemInstance[] arr = set.toArray(new ItemInstance[0]);
				set.clear();
				m.setUnread(false);
				m.setJdbcState(JdbcEntityState.UPDATED);
				m.update();   // sem anexos: apaga as linhas de mail_attachments

				for (ItemInstance it : arr)
				{
					int itemId = it.getItemId();
					long count = it.getCount();
					int enchant = it.getEnchantLevel();
					Log.LogItem(p, Log.ItemLog.PostRecieve, it);
					inv.addItem(it);
					p.sendPacket((IStaticPacket) SystemMessage2.obtainItems(itemId, count, enchant));
				}
			}
			catch (ArithmeticException e)
			{
				fail(p, op, id, "Itens demais para o inventário.");
				return;
			}
			finally
			{
				inv.writeUnlock();
			}

			if (paid > 0L)
				paySender(m, p, paid);
			ok(p, op, String.valueOf(id));
			sendRead(p, 0, m);
		}
	}

	/** Cobrança paga: o remetente online recebe na hora; offline (ou sem espaço), por correio. */
	private static void paySender(Mail m, Player payer, long amount)
	{
		Player s = World.getPlayer(m.getSenderId());
		if (s != null && s.isOnline() && !s.isInOfflineMode()
				&& s.getInventory().validateCapacity(ADENA, amount)
				&& s.getAdena() + amount <= Integer.MAX_VALUE)
		{
			s.addAdena(amount, true);
			Log.LogItem(s, Log.ItemLog.PostPrice, ADENA, amount);
			s.sendMessage(payer.getName() + " pagou " + AuctionHouseService.fmt(amount) + " Adena pelo correio \"" + m.getTopic() + "\".");
			ui(s, "PAID", m.getMessageId() + "|" + MailText.out(payer.getName()) + "|" + amount);
			return;
		}
		List<long[]> items = new ArrayList<>();
		items.add(new long[] { ADENA, amount, 0 });
		boolean okMail = sendSystemMail(m.getSenderId(), payer.getName(),
				"Pagamento recebido: " + m.getTopic(),
				payer.getName() + " pagou " + AuctionHouseService.fmt(amount) + " Adena pela cobrança do correio \"" + m.getTopic() + "\".",
				items, true);
		if (!okMail)
			_log.error("Mail: ERRO ao pagar " + amount + " Adena ao objId " + m.getSenderId() + " (correio " + m.getMessageId()
					+ ", pago por " + payer.getName() + ") — entregar manualmente.");
	}

	// =====================================================================
	// Devolver (o destinatário recusa)
	// =====================================================================

	private static Mail returnedCopy(Mail m)
	{
		String topic = m.getTopic() == null ? "" : m.getTopic();
		Mail r = m.reject();   // inverte remetente/destinatário e leva os anexos (tipo do sistema)
		if (!topic.startsWith(RETURNED_TAG))
			topic = RETURNED_TAG + topic;
		if (topic.length() > 120)
			topic = topic.substring(0, 120);
		r.setTopic(topic);
		r.setPrice(0L);
		r.setExpireTime(now() + MailConfig.RETURNED_EXPIRE_HOURS * 3600);
		return r;
	}

	public static void giveBack(Player p, int id)
	{
		final String op = "return";
		if (!throttle(p))
			return;
		String why = busy(p);
		if (why == null)
			why = peace(p, true);
		if (why != null)
		{
			fail(p, op, id, why);
			return;
		}
		synchronized (lock())
		{
			Mail m = find(p, 0, id);
			if (m == null)
			{
				fail(p, op, id, "Este correio não existe mais.");
				sendBox(p, 0, 1);
				return;
			}
			if (m.getType() != Mail.SenderType.NORMAL || m.getAttachments().isEmpty())
			{
				fail(p, op, id, "Só correio de jogador com itens pode ser devolvido.");
				sendRead(p, 0, m);
				return;
			}
			// grava a devolução primeiro (o REPLACE leva os anexos para o correio
			// novo); só então apaga o original
			Mail r = returnedCopy(m);
			r.save();
			if (r.getMessageId() <= 0)
			{
				for (ItemInstance it : r.getAttachments())
					m.addAttachment(it);
				_log.error("Mail: ERRO ao gravar a devolução do correio " + id + " — nada mudou.");
				fail(p, op, id, "Não foi possível devolver agora. Tente de novo.");
				sendRead(p, 0, m);
				return;
			}
			if (!saved(r))
				_log.error("Mail: devolução do correio " + id + " gravada pela metade (novo #" + r.getMessageId()
						+ ", itens do objId " + r.getReceiverId() + ") — conferir no banco.");
			MailDAO.getInstance().deleteReceivedMailByMailId(m.getReceiverId(), id);
			MailDAO.getInstance().deleteSentMailByMailId(m.getSenderId(), id);
			m.delete();
			PENDING.remove(id);
			Player s = World.getPlayer(r.getReceiverId());
			if (s != null && s.isOnline())
				notifyNew(s, p.getName());
			ok(p, op, String.valueOf(id));
			sendBox(p, 0, 1);
		}
	}

	// =====================================================================
	// Cancelar envio (remetente)
	// =====================================================================

	public static void cancel(Player p, int id)
	{
		final String op = "cancel";
		if (!throttle(p))
			return;
		synchronized (lock())
		{
			Mail m = find(p, 1, id);
			if (m == null)
			{
				fail(p, op, id, "Este correio não existe mais.");
				sendBox(p, 1, 1);
				return;
			}
			String why = cancelLocked(p, m);
			if (why != null)
			{
				fail(p, op, id, why);
				if (!m.isUnread())
					sendRead(p, 1, m);
				return;
			}
			ok(p, op, String.valueOf(id));
			sendBox(p, 1, 1);
		}
	}

	/**
	 * Cancela um correio enviado que o destinatário ainda não abriu: os itens
	 * voltam para o remetente (a postagem não) e o correio some dos dois lados.
	 * Chamar dentro do lock(). null = cancelado; senão, o motivo.
	 */
	private static String cancelLocked(Player p, Mail m)
	{
		int id = m.getMessageId();
		if (!m.isUnread())
			return "O destinatário já abriu: não dá mais para cancelar.";
		Set<ItemInstance> set = m.getAttachments();
		if (!set.isEmpty())
		{
			String why = busy(p);
			if (why == null)
				why = peace(p, true);
			if (why != null)
				return why;
			PcInventory inv = p.getInventory();
			inv.writeLock();
			try
			{
				String full = fitProblem(inv, set);
				if (full != null)
					return full;
				ItemInstance[] arr = set.toArray(new ItemInstance[0]);
				set.clear();
				m.setJdbcState(JdbcEntityState.UPDATED);
				m.update();
				for (ItemInstance it : arr)
				{
					int itemId = it.getItemId();
					long count = it.getCount();
					int enchant = it.getEnchantLevel();
					Log.LogItem(p, Log.ItemLog.PostCancel, it);
					inv.addItem(it);
					p.sendPacket((IStaticPacket) SystemMessage2.obtainItems(itemId, count, enchant));
				}
			}
			catch (ArithmeticException e)
			{
				return "Itens demais para o inventário.";
			}
			finally
			{
				inv.writeUnlock();
			}
		}

		MailDAO.getInstance().deleteReceivedMailByMailId(m.getReceiverId(), id);
		MailDAO.getInstance().deleteSentMailByMailId(m.getSenderId(), id);
		m.delete();
		PENDING.remove(id);
		Player r = World.getPlayer(m.getReceiverId());
		if (r != null && r.isOnline())
			ui(r, "GONE", String.valueOf(id));
		return null;
	}

	// =====================================================================
	// Excluir
	// =====================================================================

	/**
	 * Excluir. Recebidos: só sem itens e já lido. Enviados: o que o
	 * destinatário ainda não abriu é CANCELADO (itens voltam, some dos dois
	 * lados); já aberto, só sem itens (some só a cópia do remetente).
	 */
	public static void delete(Player p, int tab, int page, int[] ids)
	{
		if (tab != 1)
			tab = 0;
		int done = 0;
		int kept = 0;
		String why = null;
		synchronized (lock())
		{
			for (int id : ids)
			{
				Mail m = find(p, tab, id);
				if (m == null)
					continue;
				if (tab == 1 && m.isUnread())
				{
					String r = cancelLocked(p, m);
					if (r == null)
						done++;
					else
					{
						kept++;
						if (why == null)
							why = r;
					}
					continue;
				}
				if (!m.getAttachments().isEmpty() || (tab == 0 && m.isUnread()))
				{
					kept++;
					continue;
				}
				if (tab == 0)
					MailDAO.getInstance().deleteReceivedMailByMailId(p.getObjectId(), id);
				else
					MailDAO.getInstance().deleteSentMailByMailId(p.getObjectId(), id);
				done++;
				// ninguém mais vê o correio: apaga a linha também
				if (mysql.simple_get_int("COUNT(*)", "character_mail", "message_id=" + id) <= 0)
				{
					m.delete();
					PENDING.remove(id);
				}
			}
		}
		ok(p, "delete", done + "|" + kept);
		if (why != null)
			fail(p, "delete", 0, why);
		sendBox(p, tab, page);
	}

	// =====================================================================
	// Escrever
	// =====================================================================

	/** CFG + itens que podem ir + contatos. */
	public static void writeOpen(Player p)
	{
		ui(p, "CFG", MailConfig.FEE_BASE
				+ "|" + MailConfig.FEE_PER_ITEM
				+ "|" + MailConfig.MAX_ATTACHMENTS
				+ "|" + MailConfig.MAX_TOPIC
				+ "|" + MailConfig.MAX_BODY
				+ "|" + (MailConfig.PEACE_ZONE ? 1 : 0)
				+ "|" + (MailConfig.PEACE_ZONE_ITEMS ? 1 : 0)
				+ "|" + MailConfig.COD_EXPIRE_HOURS
				+ "|" + MailConfig.DELIVERY_DELAY
				+ "|" + MailConfig.MIN_LEVEL
				+ "|" + MailConfig.MAX_AMOUNT
				+ "|" + (MailConfig.ALLOW_MAIL ? 1 : 0));

		List<ItemInstance> items = new ArrayList<>();
		for (ItemInstance it : p.getInventory().getItems())
			if (sendable(p, it))
				items.add(it);

		ui(p, "INV_START", "");
		StringBuilder sb = new StringBuilder();
		int inLine = 0;
		for (ItemInstance it : items)
		{
			if (inLine > 0)
				sb.append(',');
			sb.append(it.getObjectId());
			if (++inLine == INV_PER_LINE)
			{
				ui(p, "INV", sb.toString());
				sb.setLength(0);
				inLine = 0;
			}
		}
		if (inLine > 0)
			ui(p, "INV", sb.toString());
		ui(p, "INV_END", String.valueOf(items.size()));
		sendContacts(p);
	}

	/** Pode ir como anexo. A Adena também (como item, com a quantidade). */
	public static boolean sendable(Player p, ItemInstance it)
	{
		return it != null && it.getCount() > 0 && !it.isEquipped() && (it.getItemId() == ADENA || it.canBeTraded(p));
	}

	public static void send(Player p, int type, long price, long adena, int[] objIds, long[] counts,
							String toName, String topic, String body)
	{
		final String op = "send";
		if (!MailConfig.ALLOW_MAIL)
		{
			fail(p, op, 0, "O correio está desligado neste servidor.");
			return;
		}
		if (!throttle(p))
			return;
		long t0 = System.currentTimeMillis();
		Long last = LAST_SEND.get(p.getObjectId());
		if (last != null && !p.isGM() && t0 - last < MailConfig.SEND_COOLDOWN * 1000L)
		{
			long s = (MailConfig.SEND_COOLDOWN * 1000L - (t0 - last) + 999L) / 1000L;
			fail(p, op, 0, "Aguarde " + s + " s para enviar outro correio.");
			return;
		}
		String why = busy(p);
		if (why != null)
		{
			fail(p, op, 0, why);
			return;
		}
		if (p.getLevel() < MailConfig.MIN_LEVEL && !p.isGM())
		{
			fail(p, op, 0, "Nível mínimo para enviar correio: " + MailConfig.MIN_LEVEL + ".");
			return;
		}

		toName = MailText.line(toName, 35);
		topic = MailText.line(topic, MailConfig.MAX_TOPIC);
		body = MailText.body(body, MailConfig.MAX_BODY);
		if (toName.isEmpty() || !MailText.validName(toName))
		{
			fail(p, op, 0, "Escreva o nome de quem vai receber.");
			return;
		}
		if (topic.isEmpty())
		{
			fail(p, op, 0, "Escreva o assunto.");
			return;
		}
		if (toName.equalsIgnoreCase(p.getName()))
		{
			fail(p, op, 0, "Você não pode enviar correio para você mesmo.");
			return;
		}

		int n = objIds.length;
		boolean cod = type == 1;
		if (n > MailConfig.MAX_ATTACHMENTS)
		{
			fail(p, op, 0, "No máximo " + MailConfig.MAX_ATTACHMENTS + " itens por correio.");
			return;
		}
		if (adena < 0L || adena > MailConfig.MAX_AMOUNT || price < 0L || price > MailConfig.MAX_AMOUNT)
		{
			fail(p, op, 0, "Valor inválido.");
			return;
		}
		if (cod)
		{
			if (price <= 0L)
			{
				fail(p, op, 0, "Informe o valor da cobrança.");
				return;
			}
			if (n == 0)
			{
				fail(p, op, 0, "A cobrança precisa de pelo menos 1 item anexado.");
				return;
			}
		}
		else
			price = 0L;

		boolean withItems = n > 0 || adena > 0L || cod;
		why = peace(p, withItems);
		if (why != null)
		{
			fail(p, op, 0, why);
			return;
		}
		if (withItems && (!p.getPlayerAccess().UseTrade || p.isTradeBannedByGM()))
		{
			fail(p, op, 0, "Sua conta não pode negociar itens.");
			return;
		}

		// destinatário
		Player to = World.getPlayer(toName);
		int toId;
		String toCanon;
		if (to != null)
		{
			toId = to.getObjectId();
			toCanon = to.getName();
		}
		else
		{
			toId = CharacterDAO.getInstance().getObjectIdByName(toName);
			toCanon = toId > 0 ? CharacterDAO.getInstance().getNameByObjectId(toId) : null;
			if (toCanon == null || toCanon.isEmpty())
				toCanon = toName;
		}
		if (toId <= 0)
		{
			fail(p, op, 0, "Personagem não encontrado: " + toName + ".");
			return;
		}
		if (toId == p.getObjectId())
		{
			fail(p, op, 0, "Você não pode enviar correio para você mesmo.");
			return;
		}
		if (p.isInBlockList(toId))
		{
			fail(p, op, 0, "Você bloqueou " + toCanon + ".");
			return;
		}
		boolean blocked = to != null ? to.isInBlockList(p)
				: mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + toId + " AND target_Id=" + p.getObjectId()) > 0;
		if (blocked)
		{
			fail(p, op, 0, toCanon + " não aceita correio seu.");
			return;
		}
		if (receivedCount(toId) >= MailConfig.INBOX_LIMIT)
		{
			fail(p, op, 0, "A caixa de " + toCanon + " está cheia.");
			return;
		}

		long fee = MailConfig.FEE_BASE + MailConfig.FEE_PER_ITEM * (n + (adena > 0L ? 1 : 0));
		Mail m = null;
		synchronized (lock())
		{
			PcInventory inv = p.getInventory();
			List<ItemInstance> moved = new ArrayList<>();
			boolean feeTaken = false;
			inv.writeLock();
			try
			{
				Set<Integer> seen = new HashSet<>();
				long adenaItems = 0L;   // Adena anexada como item (sai da mesma pilha da postagem)
				for (int i = 0; i < n; i++)
				{
					ItemInstance it = inv.getItemByObjectId(objIds[i]);
					if (counts[i] < 1L || !seen.add(objIds[i]) || (it != null && !it.isStackable() && counts[i] != 1L))
					{
						fail(p, op, 0, "Anexos inválidos.");
						return;
					}
					if (it == null || it.getCount() < counts[i])
					{
						fail(p, op, 0, "Um dos itens não está mais no inventário. Abra o envio de novo.");
						return;
					}
					if (!sendable(p, it))
					{
						fail(p, op, 0, it.getName() + " não pode ser enviado.");
						return;
					}
					if (it.getItemId() == ADENA)
						adenaItems = SafeMath.addAndCheck(adenaItems, counts[i]);
				}
				long need = SafeMath.addAndCheck(SafeMath.addAndCheck(fee, adena), adenaItems);
				if (p.getAdena() < need)
				{
					fail(p, op, 0, "Adena insuficiente: precisa de " + AuctionHouseService.fmt(need)
							+ " (postagem " + AuctionHouseService.fmt(fee) + ").");
					return;
				}
				if (fee > 0L)
				{
					if (!p.reduceAdena(fee, true))
					{
						fail(p, op, 0, "Não foi possível cobrar a postagem.");
						return;
					}
					feeTaken = true;
				}
				if (adena > 0L)
				{
					ItemInstance a = inv.getItemByItemId(ADENA);
					ItemInstance r = a == null ? null : inv.removeItemByObjectId(a.getObjectId(), adena);
					if (r == null)
						throw new IllegalStateException("Adena sumiu");
					moved.add(toMail(p, r));
				}
				for (int i = 0; i < n; i++)
				{
					ItemInstance r = inv.removeItemByObjectId(objIds[i], counts[i]);
					if (r == null)
						throw new IllegalStateException("item " + objIds[i] + " sumiu");
					moved.add(toMail(p, r));
				}

				m = new Mail();
				m.setSenderId(p.getObjectId());
				m.setSenderName(p.getName());
				m.setReceiverId(toId);
				m.setReceiverName(toCanon);
				m.setTopic(topic);
				m.setBody(body);
				m.setPrice(price);
				m.setUnread(true);
				m.setType(Mail.SenderType.NORMAL);
				m.setExpireTime(now() + (cod ? MailConfig.COD_EXPIRE_HOURS : MailConfig.EXPIRE_HOURS) * 3600);
				for (ItemInstance it : moved)
					m.addAttachment(it);
				m.save();
				if (!saved(m))
					throw new IllegalStateException("o banco não gravou o correio");
			}
			catch (Exception e)
			{
				// desfaz: correio gravado pela metade, itens e postagem de volta
				dropPartial(m);
				for (ItemInstance it : moved)
				{
					try
					{
						inv.addItem(it);
					}
					catch (Exception e2)
					{
						_log.error("Mail: ERRO devolvendo " + it + " ao objId " + p.getObjectId() + " — conferir.", e2);
					}
				}
				if (feeTaken)
					p.addAdena(fee, true);
				_log.warn("Mail: envio de " + p.getName() + " desfeito: " + e);
				fail(p, op, 0, "Não foi possível enviar. Nada saiu do seu inventário.");
				return;
			}
			finally
			{
				inv.writeUnlock();
			}

			LAST_SEND.put(p.getObjectId(), t0);
			Log.add("Mail: " + p.getName() + " -> " + toCanon + " #" + m.getMessageId() + " itens=" + n
					+ " adena=" + adena + " cobranca=" + price + " postagem=" + fee, "mail");
			deliver(m);
		}
		addContact(p, toId, toCanon);
		ok(p, op, String.valueOf(m.getMessageId()));
		sendBox(p, 1, 1);
	}

	private static ItemInstance toMail(Player p, ItemInstance it)
	{
		it.setOwnerId(p.getObjectId());
		it.setLocation(ItemInstance.ItemLocation.MAIL);
		it.save();
		Log.LogItem(p, Log.ItemLog.PostSend, it);
		return it;
	}

	// =====================================================================
	// Correio do sistema (leilão, pagamento de cobrança, teste do GM)
	// =====================================================================

	/**
	 * Correio do sistema para qualquer personagem, online ou offline, com até
	 * 8 itens criados na hora (long[]{itemId, quantidade, enchant}). Mais de 8
	 * (ou item não empilhável em quantidade) vira mais de um correio. Não tem
	 * limite de caixa nem atraso, e não se devolve: se ninguém retirar, o
	 * prazo é estendido (o item nunca some).
	 *
	 * @return true = tudo gravado
	 */
	public static boolean sendSystemMail(int toId, String senderName, String topic, String body, List<long[]> items, boolean notify)
	{
		return sendSystemMailRest(toId, senderName, topic, body, items, notify).isEmpty();
	}

	/**
	 * Igual ao sendSystemMail, mas devolve o que NÃO foi entregue
	 * (long[]{itemId, quantidade, enchant}); lista vazia = tudo gravado. Quem
	 * chama pode entregar o resto de outro jeito sem duplicar o que já foi.
	 */
	public static List<long[]> sendSystemMailRest(int toId, String senderName, String topic, String body, List<long[]> items, boolean notify)
	{
		// itens não empilháveis em quantidade viram unidades; pilhas acima do
		// limite de uma pilha viram mais de uma
		List<long[]> units = new ArrayList<>();
		List<long[]> rest = new ArrayList<>();
		if (items != null)
			for (long[] it : items)
			{
				if (it == null || it.length < 2 || it[0] <= 0 || it[1] <= 0)
					continue;
				long ench = it.length > 2 ? it[2] : 0L;
				ItemTemplate t = l2.gameserver.data.xml.holder.ItemHolder.getInstance().getTemplate((int) it[0]);
				if (t == null)
				{
					rest.add(new long[] { it[0], it[1], ench });
					continue;
				}
				if (t.isStackable())
				{
					for (long left = it[1]; left > 0; left -= Integer.MAX_VALUE)
						units.add(new long[] { it[0], Math.min(left, Integer.MAX_VALUE), ench });
				}
				else
				{
					long k = 0;
					for (; k < it[1] && k < 1000; k++)
						units.add(new long[] { it[0], 1L, ench });
					if (k < it[1])
						rest.add(new long[] { it[0], it[1] - k, ench });
				}
			}

		if (units.isEmpty() && !rest.isEmpty())
			return rest;   // só item desconhecido: não manda correio vazio

		String toName;
		Player online = World.getPlayer(toId);
		if (online != null)
			toName = online.getName();
		else
			toName = toId > 0 ? CharacterDAO.getInstance().getNameByObjectId(toId) : null;
		if (toId <= 0 || toName == null || toName.isEmpty())
		{
			_log.error("Mail: correio do sistema para objId inexistente " + toId + " (" + topic + ")");
			rest.addAll(units);
			return rest;
		}

		String sender = MailText.line(senderName, 32);
		String subj = MailText.line(topic, 100);
		String text = MailText.body(body, 1000);
		boolean any = false;
		int from = 0;
		do
		{
			int to = Math.min(units.size(), from + 8);
			Mail m = new Mail();
			List<ItemInstance> created = new ArrayList<>();
			synchronized (lock())
			{
				try
				{
					for (int i = from; i < to; i++)
					{
						long[] u = units.get(i);
						ItemInstance it = ItemFunctions.createItem((int) u[0]);
						it.setCount(u[1]);
						if (u[2] > 0)
							it.setEnchantLevel((int) u[2]);
						it.setOwnerId(toId);
						it.setLocation(ItemInstance.ItemLocation.MAIL);
						it.save();
						created.add(it);
						m.addAttachment(it);
					}
					m.setSenderId(0);
					m.setSenderName(sender.isEmpty() ? "Sistema" : sender);
					m.setReceiverId(toId);
					m.setReceiverName(toName);
					m.setTopic(subj.isEmpty() ? "Correio do sistema" : subj);
					m.setBody(text);
					m.setPrice(0L);
					m.setUnread(true);
					m.setType(Mail.SenderType.NEWS_INFORMER);
					m.setExpireTime(now() + MailConfig.SYSTEM_EXPIRE_HOURS * 3600);
					m.save();
					if (!saved(m))
						throw new IllegalStateException("o banco não gravou o correio");
					any = true;
					for (ItemInstance it : created)
						Log.add("Mail: sistema -> " + toName + " #" + m.getMessageId() + " " + it.getItemId() + " x" + it.getCount()
								+ " +" + it.getEnchantLevel() + " (" + subj + ")", "mail");
				}
				catch (Exception e)
				{
					dropPartial(m);
					for (ItemInstance it : created)
					{
						try
						{
							it.delete();
						}
						catch (Exception e2)
						{
							// nada
						}
					}
					for (int i = from; i < to; i++)
						rest.add(units.get(i));
					_log.error("Mail: ERRO no correio do sistema para objId " + toId + " (" + subj + "): " + e);
				}
			}
			from = to;
		}
		while (from < units.size());

		if (any && notify && online != null && online.isOnline())
			notifyNew(online, sender);
		return rest;
	}

	// =====================================================================
	// Contatos
	// =====================================================================

	public static void sendContacts(Player p)
	{
		StringBuilder sb = new StringBuilder();
		IntObjectMap<String> pf = p.getPostFriends();
		if (pf != null)
		{
			List<String> names = new ArrayList<>(pf.values());
			names.removeIf(s -> s == null || s.isEmpty());
			names.sort(String.CASE_INSENSITIVE_ORDER);
			for (String s : names)
			{
				if (sb.length() > 0)
					sb.append(',');
				sb.append(MailText.out(s));
			}
		}
		ui(p, "CONTACTS", sb.toString());
	}

	public static void addContact(Player p, int id, String name)
	{
		try
		{
			IntObjectMap<String> pf = p.getPostFriends();
			if (pf == null || MailConfig.MAX_CONTACTS <= 0 || pf.containsKey(id) || pf.size() >= MailConfig.MAX_CONTACTS)
				return;
			CharacterPostFriendDAO.getInstance().insert(p, id);
			pf.put(id, name);
		}
		catch (Exception e)
		{
			_log.warn("Mail: contato não salvo: " + e.getMessage());
		}
	}

	public static void removeContact(Player p, String name)
	{
		IntObjectMap<String> pf = p.getPostFriends();
		if (pf == null || name == null)
			return;
		int found = 0;
		for (IntObjectMap.Entry<String> e : pf.entrySet())
			if (name.equalsIgnoreCase(e.getValue()))
			{
				found = e.getKey();
				break;
			}
		if (found != 0)
		{
			CharacterPostFriendDAO.getInstance().delete(p, found);
			pf.remove(found);
		}
		sendContacts(p);
	}

	// =====================================================================
	// Aviso de login
	// =====================================================================

	public static void registerLoginNotice()
	{
		if (System.getProperties().putIfAbsent("l2.mail.loginNotice", "1") != null)
			return;
		PlayerListenerList.addGlobal(new OnPlayerEnterListener()
		{
			@Override
			public void onPlayerEnter(Player player)
			{
				final int objId = player.getObjectId();
				ThreadPoolManager.getInstance().schedule(() -> loginNotice(objId), 10000L);
			}
		});
	}

	private static void loginNotice(int objId)
	{
		Player p = World.getPlayer(objId);
		if (p == null || !p.isOnline())
			return;
		try
		{
			int unread;
			synchronized (lock())
			{
				unread = unreadCount(objId);
			}
			if (unread <= 0)
				return;
			p.sendPacket(ExMailArrived.STATIC);
			p.sendPacket(Msg.YOUVE_GOT_MAIL);
			ui(p, "UNREAD", String.valueOf(unread));
		}
		catch (Exception e)
		{
			_log.warn("Mail: aviso de login falhou para objId " + objId + ": " + e.getMessage());
		}
	}
}
