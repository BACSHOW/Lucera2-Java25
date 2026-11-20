package services.auctionhouse;

import java.util.ArrayList;
import java.util.List;

import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.mail.MailConfig;
import services.mail.MailService;

/**
 * Toda entrega do leilão passa por aqui: item comprado/vencido, item que
 * volta ao vendedor, pagamento da venda e lance devolvido.
 *
 *  - online: direto no inventário;
 *  - offline: pelo DelayedItems (chega no próximo login).
 *
 * Com MailAuctionDelivery = True (mail.properties) o leilão entrega pelo
 * correio (deliverItem / deliverCurrency): compra na hora e lance devolvido
 * vão direto ao inventário quando o jogador está online e cabe; o resto, ou
 * o que não couber, ou quem estiver offline, chega como correio da "Casa de
 * Leilões". Se o correio falhar, entrega como antes (nada se perde).
 *
 * deliverItem / deliverCurrency nunca lançam erro: uma entrega que falha vira
 * log ("entregar manualmente") e o resto da operação continua (o dinheiro já
 * foi cobrado, então parar no meio seria pior).
 */
public final class AuctionDelivery
{
	private static final Logger _log = LoggerFactory.getLogger(AuctionDelivery.class);

	private AuctionDelivery()
	{
	}

	// ── Pelo correio ─────────────────────────────────────────────────
	public static final int K_WON = 1;          // venceu o leilão: o item
	public static final int K_BOUGHT = 2;       // comprou na hora: o item (direto se couber)
	public static final int K_SOLD = 3;         // vendeu: a moeda, sem a taxa
	public static final int K_BID_BACK = 4;     // lance superado: a moeda (direto se couber)
	public static final int K_CANCEL_ITEM = 5;  // anúncio cancelado: o item ao vendedor
	public static final int K_CANCEL_BID = 6;   // anúncio cancelado: o lance ao licitante (direto se couber)
	public static final int K_NO_SALE = 7;      // terminou sem venda: o item

	public static boolean byMail()
	{
		return MailConfig.AUCTION_DELIVERY;
	}

	/** Para os avisos: "ao seu inventario" ou "pelo correio". */
	public static String where()
	{
		return byMail() ? "pelo correio" : "ao seu inventario";
	}

	/** "+12 Draconic Bow" / "Crystal: S-Grade x500" */
	public static String label(AuctionHolder a, long count)
	{
		String s = AuctionUtils.formatItemName(a.getItemId(), a.getEnchant());
		return count > 1 ? s + " x" + AuctionHouseService.fmt(count) : s;
	}

	/** "1,250,000 Adena" */
	public static String money(long v, int costId)
	{
		return AuctionHouseService.fmt(v) + " " + AuctionUtils.getCurrencyNameById(costId);
	}

	public static void deliverItem(int kind, int ownerId, int itemId, long count, int enchant,
		String label, String detail, String reason)
	{
		deliver(kind, ownerId, itemId, count, enchant, false, label, detail, reason);
	}

	public static void deliverCurrency(int kind, int ownerId, int itemId, long count,
		String label, String detail, String reason)
	{
		deliver(kind, ownerId, itemId, count, 0, true, label, detail, reason);
	}

	private static void deliver(int kind, int ownerId, int itemId, long count, int enchant, boolean currency,
		String label, String detail, String reason)
	{
		if (ownerId <= 0 || itemId <= 0 || count <= 0)
			return;
		try
		{
			if (byMail())
			{
				boolean direct = kind == K_BOUGHT || kind == K_BID_BACK || kind == K_CANCEL_BID;
				if (!direct || !fitsNow(ownerId, itemId, count))
				{
					List<long[]> items = new ArrayList<>();
					items.add(new long[] { itemId, count, enchant });
					List<long[]> rest = MailService.sendSystemMailRest(ownerId, MailConfig.AUCTION_SENDER, subject(kind, label), detail, items, true);
					if (rest.isEmpty())
						return;
					// entrega como antes só o que o correio não gravou (sem duplicar)
					long left = 0L;
					for (long[] r : rest)
						left += r[1];
					_log.warn("AuctionHouse: o correio falhou para " + left + " de " + count + "; entregando como antes (" + reason + ")");
					count = left;
				}
			}
			if (currency)
				giveCurrency(ownerId, itemId, count, reason);
			else
				giveItem(ownerId, itemId, count, enchant, reason);
		}
		catch (Exception e)
		{
			_log.error("AuctionHouse: ERRO ao entregar " + count + " do item " + itemId + " +" + enchant + " ao objId "
				+ ownerId + " (" + reason + ") — entregar manualmente. " + e);
		}
	}

	/** Online, fora do modo loja offline e cabe (espaço e peso) agora. */
	private static boolean fitsNow(int ownerId, int itemId, long count)
	{
		Player p = World.getPlayer(ownerId);
		if (p == null || !p.isOnline() || p.isInOfflineMode())
			return false;
		return AuctionHouseService.inventoryProblem(p, itemId, count) == null;
	}

	private static String subject(int kind, String label)
	{
		switch (kind)
		{
			case K_WON: return "Leilão vencido: " + label;
			case K_BOUGHT: return "Compra concluída: " + label;
			case K_SOLD: return "Venda concluída: " + label;
			case K_BID_BACK: return "Lance devolvido: " + label;
			case K_CANCEL_ITEM: return "Anúncio cancelado: " + label;
			case K_CANCEL_BID: return "Leilão cancelado: " + label;
			case K_NO_SALE: return "Sem venda: " + label;
			default: return "Leilão: " + label;
		}
	}

	/** Item (com enchant) para o jogador. */
	public static void giveItem(int ownerId, int itemId, long count, int enchant, String reason)
	{
		if (ownerId <= 0 || itemId <= 0 || count <= 0)
			return;

		Player p = World.getPlayer(ownerId);
		if (p != null && p.isOnline())
		{
			ItemInstance item = ItemFunctions.createItem(itemId);
			item.setCount(count);
			item.setEnchantLevel(enchant);
			Log.LogItem(p, Log.ItemLog.AuctionItemRefundReturn, item);
			p.getInventory().addItem(item);
			p.sendPacket((IStaticPacket) SystemMessage2.obtainItems(itemId, count, enchant));
			p.sendPacket(new InventoryUpdate());
			return;
		}
		delayed(ownerId, itemId, count, enchant, reason);
	}

	/** Moeda (sem enchant) para o jogador. */
	public static void giveCurrency(int ownerId, int itemId, long count, String reason)
	{
		if (ownerId <= 0 || itemId <= 0 || count <= 0)
			return;

		Player p = World.getPlayer(ownerId);
		if (p != null && p.isOnline())
		{
			ItemFunctions.addItem(p, itemId, count, true);
			return;
		}
		delayed(ownerId, itemId, count, 0, reason);
	}

	// O DelayedItems guarda a quantidade como int: acima disso vai em partes
	private static void delayed(int ownerId, int itemId, long count, int enchant, String reason)
	{
		long left = count;
		while (left > 0)
		{
			int part = (int) Math.min(left, Integer.MAX_VALUE);
			DelayedItemsManager.getInstance().addDelayed(ownerId, itemId, part, enchant, reason);
			left -= part;
		}
	}
}
