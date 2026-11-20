package services.auctionhouse;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;

/**
 * Avisos (chat + mensagem de tela) do fim de um leilão e do cancelamento, para quem está online. Só texto simples:
 * o cliente Interlude não mostra emoji.
 */
public final class AuctionNotifications
{
	/** Quem recebe o aviso de cancelamento. */
	public enum CancelType
	{
		SELLER,  // o vendedor cancelou
		BIDDER   // o licitante recebe o lance de volta
	}

	private AuctionNotifications()
	{
	}

	/** Venceu o leilão pelo tempo. */
	public static void notifyWonAuction(Player player, AuctionHolder a)
	{
		if (!online(player))
			return;
		String item = AuctionDelivery.label(a, a.getCount());
		String price = AuctionDelivery.money(a.getCurrentBid(), a.getCostId());
		player.sendMessage("Parabéns! Você venceu o leilão de " + item + " por " + price + ".");
		player.sendMessage("O item foi entregue " + AuctionDelivery.where() + ".");
		screen(player, "VITÓRIA!\n\n" + item + "\n" + price, 10000);
	}

	/** O item do vendedor foi vendido pelo tempo (já pago, com a taxa descontada). */
	public static void notifySellerPaid(Player player, AuctionHolder a)
	{
		if (!online(player))
			return;
		long price = a.getCurrentBid();
		long tax = AuctionHouseService.taxFor(price);
		String item = AuctionDelivery.label(a, a.getCount());
		player.sendMessage("Seu item " + item + " foi vendido por " + AuctionDelivery.money(price, a.getCostId())
				+ (tax > 0 ? " (taxa " + AuctionDelivery.money(tax, a.getCostId()) + ")" : "")
				+ ". Você recebe " + AuctionDelivery.money(Math.max(0L, price - tax), a.getCostId())
				+ " " + AuctionDelivery.where() + ".");
		screen(player, "ITEM VENDIDO!\n\n" + item + "\n" + AuctionDelivery.money(price, a.getCostId()), 5000);
	}

	/** O anúncio acabou sem vender (ou sem vender tudo, na venda parcial). */
	public static void notifyItemReturned(Player player, AuctionHolder a)
	{
		if (!online(player))
			return;
		player.sendMessage(AuctionTableCommunity.returnedMessage(a));
		screen(player, "ITEM DEVOLVIDO!\n\n" + AuctionDelivery.label(a, a.getCount()), 5000);
	}

	/** Anúncio cancelado: o vendedor recebe o item de volta; o licitante, o lance. */
	public static void notifyAuctionCancelled(Player player, AuctionHolder a, CancelType type)
	{
		if (!online(player))
			return;
		String item = AuctionDelivery.label(a, a.getCount());
		if (type == CancelType.SELLER)
		{
			player.sendMessage("Anúncio cancelado: " + item + " foi devolvido " + AuctionDelivery.where() + ".");
			if (a.getCurrentBid() > 0)
				player.sendMessage("O lance de " + AuctionDelivery.money(a.getCurrentBid(), a.getCostId()) + " voltou para o licitante.");
			screen(player, "ANÚNCIO CANCELADO!\n\n" + item, 5000);
			return;
		}
		String bid = AuctionDelivery.money(a.getCurrentBid(), a.getCostId());
		player.sendMessage("O vendedor cancelou o anúncio de " + item + ". Seu lance de " + bid + " foi devolvido.");
		screen(player, "LEILÃO CANCELADO!\n\n" + item + "\nSeu lance de " + bid + " foi devolvido.", 5000);
	}

	private static boolean online(Player p)
	{
		return p != null && p.isOnline();
	}

	private static void screen(Player p, String text, int ms)
	{
		p.sendPacket(new ExShowScreenMessage(text, ms, ScreenMessageAlign.TOP_CENTER, true));
	}
}
