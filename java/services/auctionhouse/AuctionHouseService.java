package services.auctionhouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.templates.item.ArmorTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lance e compra imediata da interface nativa do leilão (aba 0).
 *
 * O Interface.java só chama placeBid / buyNow / buyPartial e manda o resultado ao cliente.
 *
 * Regras (as do MarketplaceCBBypasses antigo, com estas mudanças):
 *  - tudo dentro de synchronized (auction): o mesmo cadeado do cancelar e da
 *    tarefa que encerra os leilões; dois jogadores no mesmo segundo não passam
 *    juntos;
 *  - cobra o novo lance ANTES de devolver o anterior, e confere o que saiu;
 *  - devolução para quem está offline funciona (o antigo exigia
 *    World.getPlayer != null, que é sempre null para offline);
 *  - o vendedor não compra o próprio item;
 *  - lance mínimo = lance atual + MinBidIncreasePercent % (pelo menos +1);
 *  - anti-sniping: lance nos últimos AntiSnipeMinutes estende o fim para
 *    agora + AntiSnipeExtendMinutes.
 */
public final class AuctionHouseService
{
	private static final Logger _log = LoggerFactory.getLogger(AuctionHouseService.class);

	// Um lance/compra por jogador a cada ACTION_DELAY_MS (contra clique repetido e macro)
	private static final long ACTION_DELAY_MS = 700L;
	private static final Map<Integer, Long> LAST_ACTION = new ConcurrentHashMap<>();

	// Quem está com a janela "Dar lance" aberta: objectId do jogador -> auctionId.
	// Só essas pessoas recebem as atualizações em tempo real, e só quando algo
	// muda (lance de outro jogador, compra, cancelamento, fim do tempo).
	private static final Map<Integer, Integer> WATCHING = new ConcurrentHashMap<>();

	private AuctionHouseService()
	{
	}

	/** Resultado de um lance / compra. */
	public static final class Result
	{
		public final boolean ok;
		public final String message;     // motivo da falha (ok = false)
		public final int extendedMinutes; // anti-sniping: minutos somados ao leilão (lance)

		private Result(boolean ok, String message, int extendedMinutes)
		{
			this.ok = ok;
			this.message = message;
			this.extendedMinutes = extendedMinutes;
		}

		static Result fail(String message)
		{
			return new Result(false, message, 0);
		}

		static Result ok(int extendedMinutes)
		{
			return new Result(true, "", extendedMinutes);
		}
	}

	// ================================================================
	// Regras de valor
	// ================================================================

	/** Próximo lance aceito: preço inicial (sem lances) ou atual + X% (mínimo +1). */
	public static long minBid(AuctionHolder a)
	{
		long cur = a.getCurrentBid();
		if (cur <= 0)
			return Math.max(1L, a.getStartingPrice());

		long inc = (long) Math.ceil(cur * Math.max(0, Config.AUCTION_MIN_BID_PERCENT) / 100.0);
		return cur + Math.max(1L, inc);
	}

	/** Taxa sobre a venda em centésimos de % (AuctionTaxRate 0.05 = 500). Vai para o cliente no AUC_CFG. */
	public static int taxBasisPoints()
	{
		long bp = Math.round(Config.AUCTION_TAX_RATE * 10000.0);
		return (int) Math.max(0L, Math.min(10000L, bp));
	}

	/**
	 * Taxa que sai do valor da venda, arredondada para BAIXO: nunca passa da
	 * porcentagem configurada (5% de 1 Gold Bar = 0, o vendedor recebe 1;
	 * 5% de 20 = 1, recebe 19). Conta só com inteiros, igual ao cliente
	 * ("Você recebe ..."), para os dois baterem.
	 */
	public static long taxFor(long price)
	{
		if (price <= 0)
			return 0L;
		return Math.min(price, (price * taxBasisPoints()) / 10000L);
	}

	/** Leilão só de compra imediata (preço inicial = compra imediata): não aceita lance. */
	public static boolean isBuyNowOnly(AuctionHolder a)
	{
		return a.isFixedPrice();
	}

	/**
	 * Menor compra imediata aceita num leilão com este preço inicial: o
	 * inicial + o incremento mínimo de lance (MinBidIncreasePercent, pelo menos
	 * +1). Abaixo disso o primeiro lance já alcançaria a compra imediata.
	 */
	public static long minBuyNow(long startingPrice)
	{
		long inc = (long) Math.ceil(startingPrice * Math.max(0, Config.AUCTION_MIN_BID_PERCENT) / 100.0);
		return startingPrice + Math.max(1L, inc);
	}

	/**
	 * Cabe no inventário do jogador (espaço e peso)? null = cabe; senão o
	 * motivo. Conferido ANTES de cobrar. Um erro na conferência não bloqueia
	 * a compra (fica como era antes).
	 */
	static String inventoryProblem(Player player, int itemId, long count)
	{
		try
		{
			// validateCapacity(id, n): n = espaços novos. Empilhável ocupa 1
			// (0 se o jogador já tem o item); não empilhável, 1 por unidade.
			ItemTemplate t = ItemHolder.getInstance().getTemplate(itemId);
			long slots = (t != null && t.isStackable()) ? 1L : count;
			if (!player.getInventory().validateCapacity(itemId, slots))
				return "Seu inventário está cheio. Libere espaço e tente de novo.";
			if (!player.getInventory().validateWeight(itemId, count))
				return "Peso demais para carregar isso. Libere peso e tente de novo.";
			// pilha acima de 2.147.483.647 seria cortada pelo núcleo
			if (t != null && t.isStackable())
			{
				ItemInstance have = player.getInventory().getItemByItemId(itemId);
				if (have != null && have.getCount() + count > Integer.MAX_VALUE)
					return "Você já tem o máximo desse item que dá para carregar.";
			}
		}
		catch (Exception e)
		{
			_log.warn("AuctionHouse: não consegui conferir o inventário de " + player.getName() + ": " + e);
		}
		return null;
	}

	/**
	 * O jogador esta numa situacao em que nao deve mexer em item/moeda (loja privada, troca aberta ou pedida,
	 * encantando)? null = pode; senao o motivo. Cobrar ou tirar item do inventario no meio de uma troca ou de uma
	 * loja aberta pode deixar a lista da troca/loja apontando para item que nao existe mais.
	 */
	public static String busyReason(Player player)
	{
		if (player.isInStoreMode())
			return "Feche a sua loja privada antes de usar o leilão.";
		if (player.isInTrade() || player.isProcessingRequest())
			return "Termine a troca em andamento antes de usar o leilão.";
		if (player.getEnchantScroll() != null)
			return "Termine o encantamento antes de usar o leilão.";
		return null;
	}

	/** Leilão ainda pode receber lance/compra (ativo, no mapa e com tempo). */
	private static boolean isOpen(AuctionTableCommunity table, AuctionHolder a)
	{
		return a.isActive() && table.getAuction(a.getAuctionId()) == a && a.getTimeRemaining() > 0;
	}

	// ================================================================
	// Lance
	// ================================================================

	public static Result placeBid(Player player, int auctionId, long amount)
	{
		if (!throttle(player))
			return Result.fail("Aguarde um instante e tente de novo.");
		String busy = busyReason(player);
		if (busy != null)
			return Result.fail(busy);

		AuctionTableCommunity table = AuctionTableCommunity.getInstance();
		AuctionHolder a = table.getAuction(auctionId);
		if (a == null)
			return Result.fail("Leilão não encontrado (já pode ter terminado).");

		String currency = AuctionUtils.getCurrencyNameById(a.getCostId());
		int prevBidder;
		long prevBid;
		boolean wasTop;
		int extended = 0;

		synchronized (a)
		{
			if (!isOpen(table, a))
				return Result.fail("Este leilão já foi encerrado.");

			if (a.getSellerId() == player.getObjectId())
				return Result.fail("Você não pode dar lance no seu próprio leilão.");

			if (isBuyNowOnly(a))
				return Result.fail("Este leilão é só de compra imediata. Use Comprar já.");

			if (amount <= 0 || amount > Integer.MAX_VALUE)
				return Result.fail("Valor inválido.");

			long min = minBid(a);
			if (amount < min)
				return Result.fail("O lance mínimo agora é " + fmt(min) + " " + currency + ".");

			if (a.getBuyNowPrice() > 0 && amount >= a.getBuyNowPrice())
				return Result.fail("Esse valor alcança a compra imediata (" + fmt(a.getBuyNowPrice())
					+ " " + currency + "). Use Comprar já.");

			prevBidder = a.getBidderId();
			prevBid = a.getCurrentBid();
			wasTop = prevBid > 0 && prevBidder == player.getObjectId();

			// Quem já tem o maior lance paga só a diferença
			long charge = wasTop ? amount - prevBid : amount;
			if (charge <= 0)
				return Result.fail("O lance precisa ser maior que o seu lance atual.");

			if (ItemFunctions.getItemCount(player, a.getCostId()) < charge)
				return Result.fail("Você não tem " + fmt(charge) + " " + currency + ".");

			// 1) cobra o novo lance
			long removed = ItemFunctions.removeItem(player, a.getCostId(), charge, true);
			if (removed < charge)
			{
				if (removed > 0)
					ItemFunctions.addItem(player, a.getCostId(), removed, true);
				return Result.fail("Não foi possível cobrar o lance.");
			}

			// 2) atualiza o leilão (com o anti-sniping)
			Date oldEnd = a.getEndTime();
			a.setCurrentBid(amount);
			a.setBidderId(player.getObjectId());

			if (Config.AUCTION_SNIPE_MINUTES > 0 && Config.AUCTION_SNIPE_EXTEND_MINUTES > 0
				&& a.getTimeRemaining() <= Config.AUCTION_SNIPE_MINUTES * 60000L)
			{
				long newEnd = System.currentTimeMillis() + Config.AUCTION_SNIPE_EXTEND_MINUTES * 60000L;
				if (newEnd > a.getEndTime().getTime())
				{
					a.setEndTime(new Date(newEnd));
					extended = Config.AUCTION_SNIPE_EXTEND_MINUTES;
				}
			}

			// 3) grava ANTES de devolver o lance anterior. Se o banco nao gravar, desfaz e devolve a cobranca:
			//    sem isso, no reinicio o lance anterior voltava a valer com o dinheiro ja devolvido.
			if (!table.updateAuction(a))
			{
				a.setCurrentBid(prevBid);
				a.setBidderId(prevBidder);
				a.setEndTime(oldEnd);
				ItemFunctions.addItem(player, a.getCostId(), charge, true);
				_log.warn("AuctionHouse: lance de " + player.getName() + " no leilão " + auctionId + " desfeito (banco não gravou)");
				return Result.fail("Não foi possível registrar o lance agora. Nada foi cobrado; tente de novo.");
			}

			// 4) devolve o lance anterior (de outro jogador), online ou offline
			if (!wasTop && prevBid > 0 && prevBidder > 0)
				AuctionDelivery.deliverCurrency(AuctionDelivery.K_BID_BACK, prevBidder, a.getCostId(), prevBid,
					AuctionDelivery.label(a, a.getCount()),
					"Seu lance de " + AuctionDelivery.money(prevBid, a.getCostId()) + " foi superado e voltou para você.",
					"Auction House: lance superado, leilão " + auctionId);

			table.addBid(new AuctionBid(auctionId, player.getObjectId(), amount, new Date()));
		}

		// ── fora do cadeado: avisos ─────────────────────────────────
		String itemName = AuctionUtils.formatItemName(a.getItemId(), a.getEnchant());

		_log.info("AuctionHouse: " + player.getName() + " deu lance de " + amount + " no leilão "
			+ auctionId + " (" + itemName + "), anterior=" + prevBid + " de " + prevBidder
			+ (extended > 0 ? ", estendido " + extended + " min" : ""));

		if (!wasTop && prevBid > 0 && prevBidder > 0)
			notifyOutbid(prevBidder, a, itemName, prevBid, amount, currency);

		announce(player.getName() + " deu lance de " + fmt(amount) + " " + currency + " em " + itemName + ".");

		// quem está com este leilão aberto vê o lance novo na hora
		notifyChanged(a, player.getObjectId());

		return Result.ok(extended);
	}

	// ================================================================
	// Compra imediata
	// ================================================================

	/**
	 * @param expectedPrice preço que o jogador viu na confirmação (0 = não conferir).
	 *                      Se for diferente da compra imediata atual, não compra.
	 */
	public static Result buyNow(Player player, int auctionId, long expectedPrice)
	{
		if (!throttle(player))
			return Result.fail("Aguarde um instante e tente de novo.");
		String busy = busyReason(player);
		if (busy != null)
			return Result.fail(busy);

		AuctionTableCommunity table = AuctionTableCommunity.getInstance();
		AuctionHolder a = table.getAuction(auctionId);
		if (a == null)
			return Result.fail("Leilão não encontrado (já pode ter terminado).");

		String currency = AuctionUtils.getCurrencyNameById(a.getCostId());
		int prevBidder;
		long prevBid;
		boolean wasTop;
		long price;
		long tax;

		synchronized (a)
		{
			if (!isOpen(table, a))
				return Result.fail("Este leilão já foi encerrado.");

			if (!a.hasBuyNow())
				return Result.fail("Este leilão não tem compra imediata.");

			if (a.getSellerId() == player.getObjectId())
				return Result.fail("Você não pode comprar o seu próprio item.");

			price = a.getBuyNowPrice();
			if (expectedPrice > 0 && expectedPrice != price)
				return Result.fail("O preço mudou. Abra o item de novo.");

			// confere o item ANTES de cobrar (createItem com id inválido daria erro depois)
			if (ItemHolder.getInstance().getTemplate(a.getItemId()) == null)
			{
				_log.warn("AuctionHouse: leilão " + auctionId + " com itemId inválido " + a.getItemId());
				return Result.fail("Item inválido. Avise um GM.");
			}

			// cabe no inventário? (antes de cobrar)
			// pelo correio o que não couber chega como carta: não precisa barrar
			String full = AuctionDelivery.byMail() ? null : inventoryProblem(player, a.getItemId(), a.getCount());
			if (full != null)
				return Result.fail(full);

			prevBidder = a.getBidderId();
			prevBid = a.getCurrentBid();
			wasTop = prevBid > 0 && prevBidder == player.getObjectId();

			long charge = wasTop ? price - prevBid : price;
			if (charge < 0)
				charge = 0;

			if (charge > 0 && ItemFunctions.getItemCount(player, a.getCostId()) < charge)
				return Result.fail("Você não tem " + fmt(charge) + " " + currency + ".");

			// 1) cobra
			if (charge > 0)
			{
				long removed = ItemFunctions.removeItem(player, a.getCostId(), charge, true);
				if (removed < charge)
				{
					if (removed > 0)
						ItemFunctions.addItem(player, a.getCostId(), removed, true);
					return Result.fail("Não foi possível cobrar a compra.");
				}
			}

			// 2) já pago: grava ENDED (com o comprador) ANTES de entregar. Se alguma entrega abaixo der erro, nada
			//    (tarefa de expiração, cancelar, outro lance, reinício) entrega este leilão de novo. Se o banco não
			//    gravar, desfaz e devolve a cobrança.
			a.setBidderId(player.getObjectId());
			a.setCurrentBid(price);
			if (!table.markEnded(a))
			{
				a.setBidderId(prevBidder);
				a.setCurrentBid(prevBid);
				if (charge > 0)
					ItemFunctions.addItem(player, a.getCostId(), charge, true);
				_log.warn("AuctionHouse: compra de " + player.getName() + " no leilão " + auctionId + " desfeita (banco não gravou)");
				return Result.fail("Não foi possível concluir a compra agora. Nada foi cobrado; tente de novo.");
			}

			// 3) devolve o lance de quem estava ganhando (outro jogador)
			if (!wasTop && prevBid > 0 && prevBidder > 0)
				AuctionDelivery.deliverCurrency(AuctionDelivery.K_BID_BACK, prevBidder, a.getCostId(), prevBid,
					AuctionDelivery.label(a, a.getCount()),
					"Outro jogador comprou o item na hora. Seu lance de "
						+ AuctionDelivery.money(prevBid, a.getCostId()) + " voltou para você.",
					"Auction House: item comprado por outro jogador, leilão " + auctionId);

			// 4) entrega o item ao comprador
			AuctionDelivery.deliverItem(AuctionDelivery.K_BOUGHT, player.getObjectId(), a.getItemId(), a.getCount(), a.getEnchant(),
				AuctionDelivery.label(a, a.getCount()),
				"Você comprou na hora por " + AuctionDelivery.money(price, a.getCostId()) + ".",
				"Auction House: compra imediata do leilão " + auctionId);

			// 5) paga o vendedor, já com a taxa descontada
			tax = taxFor(price);
			long net = Math.max(0L, price - tax);
			if (net > 0)
				AuctionDelivery.deliverCurrency(AuctionDelivery.K_SOLD, a.getSellerId(), a.getCostId(), net,
					AuctionDelivery.label(a, a.getCount()),
					"Comprado na hora por " + AuctionDelivery.money(price, a.getCostId())
						+ (tax > 0 ? ". Taxa: " + AuctionDelivery.money(tax, a.getCostId()) : "")
						+ ". Você recebe " + AuctionDelivery.money(net, a.getCostId()) + ".",
					"Auction House: venda do leilão " + auctionId);

			// 6) histórico (o ENDED já foi gravado no passo 2)
			table.saveHistory(a);
		}

		// ── fora do cadeado: avisos ─────────────────────────────────
		String itemName = AuctionUtils.formatItemName(a.getItemId(), a.getEnchant());

		_log.info("AuctionHouse: " + player.getName() + " comprou o leilão " + auctionId + " ("
			+ itemName + ") por " + price + ", taxa=" + tax + ", lance devolvido=" + (wasTop ? 0 : prevBid));

		if (!wasTop && prevBid > 0 && prevBidder > 0)
		{
			Player old = World.getPlayer(prevBidder);
			if (old != null && old.isOnline())
			{
				old.sendMessage(itemName + " foi comprado por compra imediata. Seu lance de "
					+ fmt(prevBid) + " " + currency + " foi devolvido.");
				old.sendMessage("AUC_OUTBID:" + a.getAuctionId() + "|" + a.getItemId() + "|" + a.getEnchant());
				sendMyBids(old);
			}
		}

		Player seller = World.getPlayer(a.getSellerId());
		if (seller != null && seller.isOnline())
		{
			seller.sendMessage("Seu item " + itemName + " foi vendido por " + fmt(price) + " " + currency
				+ (tax > 0 ? " (taxa " + fmt(tax) + ")" : "") + ".");
			screen(seller, "ITEM VENDIDO!\n\n" + itemName + "\n" + fmt(price) + " " + currency);
		}

		announce(player.getName() + " comprou " + (a.getCount() > 1 ? fmt(a.getCount()) + " " : "") + itemName
			+ " por " + fmt(price) + " " + currency + ".");

		notifyEnded(a, "Este item foi vendido por compra imediata.", player.getObjectId());

		return Result.ok(0);
	}

	// ================================================================
	// Compra parcial (preço fixo com "aceitar compra parcial")
	// ================================================================

	/**
	 * Compra só parte do anúncio. O anúncio continua com o que resta (mesmo
	 * preço por unidade) e acaba quando esgota.
	 *
	 * @param qty          quantas unidades
	 * @param expectedUnit preço da unidade que o jogador viu (0 = não conferir).
	 *                     Se mudou, não compra.
	 */
	public static Result buyPartial(Player player, int auctionId, long qty, long expectedUnit)
	{
		if (!throttle(player))
			return Result.fail("Aguarde um instante e tente de novo.");
		String busy = busyReason(player);
		if (busy != null)
			return Result.fail(busy);

		AuctionTableCommunity table = AuctionTableCommunity.getInstance();
		AuctionHolder a = table.getAuction(auctionId);
		if (a == null)
			return Result.fail("Anúncio não encontrado (já pode ter terminado).");

		String currency = AuctionUtils.getCurrencyNameById(a.getCostId());
		long unit;
		long price;
		long tax;
		int left;
		int original;
		boolean soldOut;

		synchronized (a)
		{
			if (!isOpen(table, a))
				return Result.fail("Este anúncio já foi encerrado.");

			if (!a.isPartial())
				return Result.fail("Este anúncio não aceita compra parcial. Use Comprar já.");

			if (a.getSellerId() == player.getObjectId())
				return Result.fail("Você não pode comprar o seu próprio item.");

			int have = a.getCount();
			if (qty < 1)
				return Result.fail("Quantidade inválida.");
			if (qty > have)
				return Result.fail("Restam apenas " + fmt(have) + ".");

			unit = a.getUnitPrice();
			if (unit <= 0 || unit * have != a.getStartingPrice())
			{
				_log.warn("AuctionHouse: anúncio " + auctionId + " com preço por unidade inconsistente: total="
					+ a.getStartingPrice() + " qtd=" + have);
				return Result.fail("Preço do anúncio inválido. Avise um GM.");
			}
			if (expectedUnit > 0 && expectedUnit != unit)
				return Result.fail("O preço mudou. Abra o item de novo.");

			price = unit * qty;   // <= total do anúncio, que já respeita o teto

			if (ItemHolder.getInstance().getTemplate(a.getItemId()) == null)
			{
				_log.warn("AuctionHouse: anúncio " + auctionId + " com itemId inválido " + a.getItemId());
				return Result.fail("Item inválido. Avise um GM.");
			}

			String full = AuctionDelivery.byMail() ? null : inventoryProblem(player, a.getItemId(), qty);
			if (full != null)
				return Result.fail(full);

			if (ItemFunctions.getItemCount(player, a.getCostId()) < price)
				return Result.fail("Você não tem " + fmt(price) + " " + currency + ".");

			// 1) cobra
			long removed = ItemFunctions.removeItem(player, a.getCostId(), price, true);
			if (removed < price)
			{
				if (removed > 0)
					ItemFunctions.addItem(player, a.getCostId(), removed, true);
				return Result.fail("Não foi possível cobrar a compra.");
			}

			// 2) já pago: tira do anúncio ANTES de entregar (nada entrega estas
			//    unidades de novo: outra compra, cancelar ou o fim do prazo)
			left = have - (int) qty;
			original = a.getOriginalCount();
			long oldTotal = a.getStartingPrice();
			a.setCount(left);
			a.setStartingPrice(unit * left);
			a.setBuyNowPrice(unit * left);
			soldOut = left == 0;

			// O banco precisa saber que estas unidades saíram: sem isso, depois
			// de reiniciar o anúncio voltaria com elas (venda dupla). Se a
			// gravação falhar, desfaz tudo e devolve o pagamento. O ENDED (ao
			// esgotar) só entra na memória depois que gravou: a limpeza tira
			// do mapa os ENDED sem pegar o cadeado.
			if (!table.updateRemaining(a, soldOut ? AuctionHolder.AuctionStatus.ENDED : a.getStatus()))
			{
				a.setCount(have);
				a.setStartingPrice(oldTotal);
				a.setBuyNowPrice(oldTotal);
				ItemFunctions.addItem(player, a.getCostId(), price, true);
				_log.warn("AuctionHouse: compra parcial do anúncio " + auctionId + " desfeita (banco não gravou)");
				return Result.fail("Não foi possível concluir a compra agora. Nada foi cobrado; tente de novo.");
			}
			if (soldOut)
				a.setStatus(AuctionHolder.AuctionStatus.ENDED);   // sem unidades: some da lista

			// 3) entrega ao comprador
			AuctionDelivery.deliverItem(AuctionDelivery.K_BOUGHT, player.getObjectId(), a.getItemId(), qty, a.getEnchant(),
				AuctionDelivery.label(a, qty),
				"Você comprou " + fmt(qty) + " por " + AuctionDelivery.money(price, a.getCostId()) + ".",
				"Auction House: compra parcial do anúncio " + auctionId);

			// 4) paga o vendedor com a taxa descontada. Taxa acumulada: a de
			//    tudo que já foi vendido menos a das compras anteriores, para
			//    comprar de 1 em 1 não escapar do arredondamento para baixo
			//    (a soma das taxas = a taxa do total vendido).
			long soldBefore = (long) original - have;
			tax = taxFor(unit * (soldBefore + qty)) - taxFor(unit * soldBefore);
			long net = Math.max(0L, price - tax);
			if (net > 0)
				AuctionDelivery.deliverCurrency(AuctionDelivery.K_SOLD, a.getSellerId(), a.getCostId(), net,
					AuctionDelivery.label(a, qty),
					"Venda de " + fmt(qty) + " por " + AuctionDelivery.money(price, a.getCostId())
						+ (tax > 0 ? ". Taxa: " + AuctionDelivery.money(tax, a.getCostId()) : "")
						+ ". Você recebe " + AuctionDelivery.money(net, a.getCostId()) + ".",
					"Auction House: venda parcial do anúncio " + auctionId);

			// 5) histórico (uma linha por compra) e média de preço
			table.addSaleHistory(a, player.getObjectId(), qty, price);
		}

		// ── fora do cadeado: avisos ─────────────────────────────────
		String itemName = AuctionUtils.formatItemName(a.getItemId(), a.getEnchant());

		_log.info("AuctionHouse: " + player.getName() + " comprou " + qty + " de " + (left + qty) + " do anúncio "
			+ auctionId + " (" + itemName + ") por " + price + " (" + unit + " cada), taxa=" + tax + ", restam " + left);

		Player seller = World.getPlayer(a.getSellerId());
		if (seller != null && seller.isOnline())
		{
			seller.sendMessage(partialSoldMessage(itemName, qty, price, tax, left, original, currency));
			if (soldOut)
				screen(seller, "ANÚNCIO ESGOTADO!\n\n" + itemName + "\n" + fmt(original) + " vendidas");
		}

		if (soldOut)
			notifyEnded(a, "Este anúncio esgotou.", player.getObjectId());
		else
			notifyChanged(a, player.getObjectId());   // quem está com ele aberto vê o novo "restam"

		return Result.ok(0);
	}

	/** "Vendidas 300 Crystal por 7,500 Adena (taxa 375). Restam 1,100 de 2,000." */
	static String partialSoldMessage(String itemName, long qty, long price, long tax, int left, int original, String currency)
	{
		String s = "Vendidas " + fmt(qty) + " " + itemName + " por " + fmt(price) + " " + currency
			+ (tax > 0 ? " (taxa " + fmt(tax) + ")" : "") + ".";
		if (left > 0)
			return s + " Restam " + fmt(left) + " de " + fmt(original) + ".";
		return s + " Anúncio esgotado: todas as " + fmt(original) + " foram vendidas.";
	}

	// ================================================================
	// Janela "Dar lance" aberta: detalhes e atualizações em tempo real
	// ================================================================

	/**
	 * AUC_DETAIL:id|classId|enchant|qtd|lanceAtual|lanceMínimo|compraJá|moeda|lances|tempo|flags|itemType|bodyPart|segundos
	 *   |preçoUnidade|qtdAnunciada
	 * flags: 1 = o leilão é seu, 2 = você tem o maior lance, 4 = só compra imediata (preço fixo),
	 *        8 = aceita compra parcial
	 * qtd = o que resta; qtdAnunciada = quantidade do começo (vendidas = a diferença)
	 */
	public static String detailMessage(Player player, AuctionHolder a)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(a.getItemId());
		int bodyPart = (t != null) ? t.getBodyPart() : 0;

		int flags = 0;
		if (a.getSellerId() == player.getObjectId())
			flags |= 1;
		if (a.getCurrentBid() > 0 && a.getBidderId() == player.getObjectId())
			flags |= 2;
		if (isBuyNowOnly(a))
			flags |= 4;
		if (a.isPartial())
			flags |= 8;

		return "AUC_DETAIL:" + a.getAuctionId()
			+ "|" + a.getItemId()
			+ "|" + a.getEnchant()
			+ "|" + a.getCount()
			+ "|" + a.getCurrentBid()
			+ "|" + minBid(a)
			+ "|" + a.getBuyNowPrice()
			+ "|" + a.getCostId()
			+ "|" + AuctionTableCommunity.getInstance().getBids(a.getAuctionId()).size()
			+ "|" + a.getTimeRemainingFormatted()
			+ "|" + flags
			+ "|" + itemType(t)
			+ "|" + bodyPart
			+ "|" + (a.getTimeRemaining() / 1000L)
			+ "|" + a.getUnitPrice()
			+ "|" + a.getOriginalCount();
	}

	/** ItemType do cliente: 0=arma, 1=armadura/escudo, 2=joia, 3=outros */
	public static int itemType(ItemTemplate t)
	{
		int bodyPart = (t != null) ? t.getBodyPart() : 0;

		if (bodyPart == 256)                       // escudo (L_HAND): no Lucera herda Weapon
			return 1;
		if (t instanceof WeaponTemplate)
			return 0;
		if (t instanceof ArmorTemplate)
		{
			int jBits = 2 | 4 | 8 | 16 | 32;       // R_EAR | L_EAR | NECK | R_FINGER | L_FINGER
			return ((bodyPart & jBits) != 0 && (bodyPart & ~jBits) == 0) ? 2 : 1;
		}
		return 3;
	}

	/** O jogador abriu a janela "Dar lance" neste leilão. */
	public static void watch(Player player, int auctionId)
	{
		WATCHING.put(player.getObjectId(), auctionId);
	}

	/** O jogador fechou a janela "Dar lance". */
	public static void unwatch(Player player)
	{
		WATCHING.remove(player.getObjectId());
	}

	/** Valores novos para quem está com o leilão aberto (menos exceptObjId). */
	public static void notifyChanged(AuctionHolder a, int exceptObjId)
	{
		int id = a.getAuctionId();
		for (Map.Entry<Integer, Integer> e : WATCHING.entrySet())
		{
			if (e.getValue().intValue() != id || e.getKey().intValue() == exceptObjId)
				continue;

			Player p = World.getPlayer(e.getKey().intValue());
			if (p == null || !p.isOnline())
			{
				WATCHING.remove(e.getKey(), e.getValue());
				continue;
			}
			p.sendMessage(detailMessage(p, a));
		}
	}

	/**
	 * O leilão acabou (tempo, compra imediata ou cancelamento): avisa quem está
	 * com ele aberto, para a janela mostrar "encerrado" e travar os campos.
	 * Chamado também pela tarefa de expiração do AuctionTableCommunity.
	 */
	public static void notifyEnded(AuctionHolder a, String reason, int exceptObjId)
	{
		int id = a.getAuctionId();
		for (Map.Entry<Integer, Integer> e : WATCHING.entrySet())
		{
			if (e.getValue().intValue() != id)
				continue;

			WATCHING.remove(e.getKey(), e.getValue());
			if (e.getKey().intValue() == exceptObjId)
				continue;

			Player p = World.getPlayer(e.getKey().intValue());
			if (p != null && p.isOnline())
				p.sendMessage("AUC_ENDED:" + id + "|" + reason);
		}
	}

	// ================================================================
	// Auxiliares
	// ================================================================

	private static void notifyOutbid(int bidderId, AuctionHolder a, String itemName,
		long oldBid, long newBid, String currency)
	{
		Player p = World.getPlayer(bidderId);
		if (p == null || !p.isOnline())
			return;   // offline: o valor já foi pelo DelayedItems

		p.sendMessage("Seu lance em " + itemName + " foi superado. Seus " + fmt(oldBid) + " "
			+ currency + " foram devolvidos.");
		screen(p, "LANCE SUPERADO!\n\n" + itemName + "\nLance atual: " + fmt(newBid) + " " + currency);
		// a janela do leilão, se aberta, atualiza sozinha; "My Bids (N)" na
		// árvore muda na hora (sem esperar a lista recarregar)
		p.sendMessage("AUC_OUTBID:" + a.getAuctionId() + "|" + a.getItemId() + "|" + a.getEnchant());
		sendMyBids(p);
	}

	private static void screen(Player p, String text)
	{
		try
		{
			p.sendPacket(new ExShowScreenMessage(text, 6000,
				ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false));
		}
		catch (Exception e)
		{
			// mensagem de tela é só enfeite
		}
	}

	// ================================================================
	// Avisos: lances superados (My Bids) e vendas enquanto estava offline
	// ================================================================

	/**
	 * Leilões ativos em que o jogador deu lance: {com o lance superado, total}.
	 * "Superado" = deu lance, mas o maior lance agora é de outro jogador.
	 */
	public static int[] countMyBids(Player player)
	{
		int me = player.getObjectId();
		int outbid = 0;
		int active = 0;
		AuctionTableCommunity table = AuctionTableCommunity.getInstance();

		for (AuctionHolder a : table.getAuctions().values())
		{
			if (a == null || !a.isActive() || a.getTimeRemaining() <= 0 || a.getSellerId() == me)
				continue;

			boolean mine = false;
			for (Object o : table.getBids(a.getAuctionId()).toArray())   // cópia: outro lance pode chegar
			{
				AuctionBid b = (AuctionBid) o;
				if (b != null && !b.isCancelled() && b.getBidderId() == me)
				{
					mine = true;
					break;
				}
			}
			if (!mine)
				continue;

			active++;
			if (a.getBidderId() != me)
				outbid++;
		}
		return new int[] { outbid, active };
	}

	/** AUC_MYBIDS:superados|total — "My Bids (N)" na árvore e aviso na barra. */
	public static void sendMyBids(Player player)
	{
		int[] c = countMyBids(player);
		player.sendMessage("AUC_MYBIDS:" + c[0] + "|" + c[1]);
	}

	/**
	 * Situação do jogador neste leilão, para marcar a linha na lista:
	 * 0 = não deu lance (ou o leilão é dele), 1 = tem o maior lance,
	 * 2 = deu lance, mas o maior agora é de outro jogador.
	 */
	public static int bidState(AuctionHolder a, int playerId)
	{
		if (a == null || playerId <= 0 || a.getSellerId() == playerId)
			return 0;
		if (a.getCurrentBid() > 0 && a.getBidderId() == playerId)
			return 1;

		for (Object o : AuctionTableCommunity.getInstance().getBids(a.getAuctionId()).toArray())   // cópia
		{
			AuctionBid b = (AuctionBid) o;
			if (b != null && !b.isCancelled() && b.getBidderId() == playerId)
				return 2;
		}
		return 0;
	}

	/**
	 * Liga o aviso de login ("enquanto você estava offline, N itens foram
	 * vendidos"). Chamado no Interface.onLoad; registra uma vez só, mesmo
	 * se os scripts forem recarregados.
	 */
	public static void registerLoginNotice()
	{
		if (System.getProperties().putIfAbsent("l2.auctionhouse.loginNotice", "1") != null)
			return;

		PlayerListenerList.addGlobal(new OnPlayerEnterListener()
		{
			@Override
			public void onPlayerEnter(Player player)
			{
				final int  objId = player.getObjectId();
				final long since = player.getLastAccess();   // segundos: última saída do jogo
				if (since <= 0L)
					return;

				// alguns segundos depois de entrar, para a mensagem de tela aparecer
				ThreadPoolManager.getInstance().schedule(() -> loginNotice(objId, since), 8000L);
			}
		});
	}

	private static void loginNotice(int objId, long sinceSeconds)
	{
		Player p = World.getPlayer(objId);
		if (p == null || !p.isOnline())
			return;

		int sold = 0;
		int expired = 0;

		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(
				// DISTINCT: na venda parcial cada compra é uma linha do mesmo anúncio
				"SELECT status, COUNT(DISTINCT auction_id) FROM auction_house_history "
				+ "WHERE seller_id = ? AND ended_time > ? GROUP BY status"))
		{
			st.setInt(1, objId);
			st.setTimestamp(2, new Timestamp(sinceSeconds * 1000L));
			try (ResultSet rs = st.executeQuery())
			{
				while (rs.next())
				{
					if ("sold".equalsIgnoreCase(rs.getString(1)))
						sold += rs.getInt(2);
					else if ("expired".equalsIgnoreCase(rs.getString(1)))
						expired += rs.getInt(2);
				}
			}
		}
		catch (Exception e)
		{
			_log.warn("AuctionHouse: aviso de login falhou para objId " + objId + ": " + e.getMessage());
			return;
		}

		String text = loginNoticeText(sold, expired);
		if (text.isEmpty())
			return;

		p.sendMessage("Leilão: " + text);
		screen(p, "LEILÃO\n\n" + text);
	}

	/**
	 * "3 itens seus foram vendidos e 1 voltou sem ser vendido." ("" = nada aconteceu)
	 * Venda parcial conta 1 por anúncio (um anúncio pode estar nos dois: vendeu
	 * parte e o resto voltou).
	 */
	public static String loginNoticeText(int sold, int expired)
	{
		if (sold <= 0 && expired <= 0)
			return "";

		StringBuilder sb = new StringBuilder("Enquanto você estava offline, ");
		if (sold > 0)
		{
			sb.append(sold == 1 ? "1 item seu foi vendido" : sold + " itens seus foram vendidos");
			if (expired > 0)
				sb.append(" e ").append(expired == 1 ? "1 voltou sem ser vendido" : expired + " voltaram sem ser vendidos");
		}
		else
			sb.append(expired == 1 ? "1 item seu voltou sem ser vendido" : expired + " itens seus voltaram sem ser vendidos");

		if (sold > 0 && expired > 0)
			sb.append(AuctionDelivery.byMail() ? ". O pagamento e os itens devolvidos estão no seu correio."
				: ". O pagamento e os itens devolvidos vão para o seu inventário.");
		else if (sold > 0)
			sb.append(AuctionDelivery.byMail() ? ". O pagamento está no seu correio." : ". O pagamento vai para o seu inventário.");
		else
			sb.append(AuctionDelivery.byMail() ? ". Os itens estão no seu correio." : ". Os itens voltam para o seu inventário.");
		return sb.toString();
	}

	private static void announce(String text)
	{
		if (!Config.AUCTION_ANNOUNCE)
			return;
		try
		{
			Announcements.getInstance().announceToAll(text);
		}
		catch (Exception e)
		{
			_log.warn("AuctionHouse: erro ao anunciar: " + e.getMessage());
		}
	}

	private static boolean throttle(Player player)
	{
		long now = System.currentTimeMillis();
		Long last = LAST_ACTION.put(player.getObjectId(), now);
		return last == null || now - last >= ACTION_DELAY_MS;
	}

	/** 1234567 → "1,234,567" (vírgula, igual aos números do cliente). */
	public static String fmt(long v)
	{
		return String.format(Locale.US, "%,d", v);
	}
}