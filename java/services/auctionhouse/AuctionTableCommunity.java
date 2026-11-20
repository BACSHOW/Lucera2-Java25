package services.auctionhouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Anúncios do leilão: memória (auctions / bids) + banco (auction_house_table, auction_house_bids,
 * auction_house_history) e a tarefa que encerra os leilões pelo tempo.
 *
 * Regra de todas as mudanças de estado (encerrar, cancelar, lance, compra): grava no banco ANTES de entregar item
 * ou moeda. Se a gravação falhar, nada é entregue; assim uma queda do servidor ou do banco no meio nunca entrega
 * a mesma coisa duas vezes.
 */
public class AuctionTableCommunity
{
	private static final Logger log = LoggerFactory.getLogger(AuctionTableCommunity.class);

	private static final long EXPIRE_CHECK_MS = 30000L;
	private static final long CLEANUP_MS = 60000L;

	private final Map<Integer, AuctionHolder> auctions = new ConcurrentHashMap<>();
	/** Lances por anúncio, do mais antigo ao mais novo. CopyOnWrite: lida enquanto outro lance entra. */
	private final Map<Integer, List<AuctionBid>> bids = new ConcurrentHashMap<>();
	private int maxId;

	// Colunas da venda parcial (allow_partial, original_count) existem no banco? Criadas sozinhas no load; se o
	// ALTER falhar (sem permissão), o leilão continua funcionando, só sem compra parcial.
	private volatile boolean partialColumns;

	public static AuctionTableCommunity getInstance()
	{
		return SingletonHolder._instance;
	}

	protected AuctionTableCommunity()
	{
		load();
		startAuctionExpirationTask();
	}

	// =====================================================================
	// Carga
	// =====================================================================

	private void load()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			ensurePartialColumns(con);

			try (PreparedStatement stm = con.prepareStatement("SELECT * FROM auction_house_table WHERE status IN ('active', 'pending')");
				ResultSet rset = stm.executeQuery())
			{
				Set<String> cols = columnNames(rset.getMetaData());
				boolean hasPartial = cols.contains("allow_partial");
				boolean hasOriginal = cols.contains("original_count");

				while (rset.next())
				{
					AuctionHolder auction = new AuctionHolder(
							rset.getInt("auctionid"), rset.getInt("sellerid"), rset.getInt("itemid"), rset.getInt("count"),
							rset.getInt("enchant"), rset.getInt("costid"), rset.getInt("costcount"),
							rset.getLong("starting_price"), rset.getLong("buy_now_price"),
							rset.getTimestamp("start_time"), rset.getTimestamp("end_time"));
					auction.setCurrentBid(rset.getLong("current_bid"));
					auction.setBidderId(rset.getInt("bidder_id"));
					auction.setStatus(parseStatus(rset.getString("status")));

					// anúncios antigos: sem compra parcial, quantidade anunciada = a atual
					if (hasPartial)
						auction.setAllowPartial(rset.getInt("allow_partial") == 1);
					if (hasOriginal)
						auction.setOriginalCount(rset.getInt("original_count"));

					auctions.put(auction.getAuctionId(), auction);
					maxId = Math.max(maxId, auction.getAuctionId());
				}
			}

			try (PreparedStatement stm = con.prepareStatement(
					"SELECT auction_id, bidder_id, bid_amount, bid_time FROM auction_house_bids WHERE is_cancelled = 0 ORDER BY bid_time ASC");
				ResultSet rset = stm.executeQuery())
			{
				while (rset.next())
				{
					int auctionId = rset.getInt("auction_id");
					bids.computeIfAbsent(auctionId, k -> new CopyOnWriteArrayList<>())
							.add(new AuctionBid(auctionId, rset.getInt("bidder_id"), rset.getLong("bid_amount"), rset.getTimestamp("bid_time")));
				}
			}
		}
		catch (Exception e)
		{
			log.error("AuctionTable: erro ao carregar os leilões do banco", e);
		}

		log.info("AuctionTable: Loaded " + auctions.size() + " auctions.");
	}

	/** Status gravado; um valor desconhecido vira ACTIVE (antes um valor estranho parava a carga inteira). */
	private static AuctionHolder.AuctionStatus parseStatus(String s)
	{
		try
		{
			return AuctionHolder.AuctionStatus.valueOf(s.trim().toUpperCase());
		}
		catch (Exception e)
		{
			log.warn("AuctionTable: status desconhecido '" + s + "', tratado como active.");
			return AuctionHolder.AuctionStatus.ACTIVE;
		}
	}

	/** O banco tem as colunas da venda parcial (pode criar anúncio com compra parcial). */
	public boolean supportsPartial()
	{
		return partialColumns;
	}

	/**
	 * Cria allow_partial e original_count em auction_house_table se ainda não existem (mesmo conteúdo do
	 * auction_house_venda_parcial.sql). Anúncios antigos ficam com 0 nas duas: sem compra parcial e quantidade
	 * anunciada = a atual.
	 */
	private void ensurePartialColumns(Connection con)
	{
		try
		{
			Set<String> cols;
			try (Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM auction_house_table WHERE 1 = 0"))
			{
				cols = columnNames(rs.getMetaData());
			}

			try (Statement st = con.createStatement())
			{
				if (!cols.contains("allow_partial"))
				{
					st.executeUpdate("ALTER TABLE auction_house_table ADD COLUMN allow_partial TINYINT NOT NULL DEFAULT 0");
					log.info("AuctionTable: coluna allow_partial criada.");
				}
				if (!cols.contains("original_count"))
				{
					st.executeUpdate("ALTER TABLE auction_house_table ADD COLUMN original_count INT NOT NULL DEFAULT 0");
					log.info("AuctionTable: coluna original_count criada.");
				}
			}
			partialColumns = true;
		}
		catch (Exception e)
		{
			partialColumns = false;
			log.warn("AuctionTable: não consegui criar as colunas da venda parcial (" + e.getMessage()
					+ "). Rode o auction_house_venda_parcial.sql; até lá a compra parcial fica desligada.");
		}

		checkHistoryKey(con);
	}

	/**
	 * Na venda parcial cada compra é uma linha do histórico com o mesmo auction_id. Se o histórico tiver uma chave
	 * única com auction_id, a partir da segunda compra o INSERT falha (só fica no log): avisa uma vez aqui.
	 */
	private void checkHistoryKey(Connection con)
	{
		try (ResultSet rs = con.getMetaData().getIndexInfo(con.getCatalog(), null, "auction_house_history", true, false))
		{
			Map<String, List<String>> idx = new HashMap<>();
			while (rs.next())
			{
				String name = rs.getString("INDEX_NAME");
				String col = rs.getString("COLUMN_NAME");
				if (name != null && col != null)
					idx.computeIfAbsent(name, k -> new ArrayList<>()).add(col.toLowerCase());
			}
			for (Map.Entry<String, List<String>> e : idx.entrySet())
				if (e.getValue().contains("auction_id"))
					log.warn("AuctionTable: auction_house_history tem chave única com auction_id (" + e.getKey() + ": "
							+ e.getValue() + "). A venda parcial grava uma linha por compra: troque por uma chave própria "
							+ "(veja o auction_house_venda_parcial.sql).");
		}
		catch (Exception e)
		{
			// só um aviso; sem ele tudo continua funcionando
		}
	}

	private static Set<String> columnNames(ResultSetMetaData md) throws SQLException
	{
		Set<String> s = new HashSet<>();
		for (int i = 1; i <= md.getColumnCount(); i++)
			s.add(md.getColumnName(i).toLowerCase());
		return s;
	}

	// =====================================================================
	// Gravação
	// =====================================================================

	/**
	 * Grava o anúncio novo e só então o coloca na lista. false = o banco não gravou: o anúncio não existe (quem
	 * chamou devolve o item e a taxa).
	 */
	public boolean addAuction(AuctionHolder auction)
	{
		boolean partial = partialColumns;
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(
				"INSERT INTO auction_house_table (auctionid, sellerid, itemid, count, enchant, costid, costcount, starting_price, "
					+ "current_bid, buy_now_price, bidder_id, start_time, end_time, status" + (partial ? ", allow_partial, original_count" : "")
					+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?" + (partial ? ",?,?" : "") + ")"))
		{
			stm.setInt(1, auction.getAuctionId());
			stm.setInt(2, auction.getSellerId());
			stm.setInt(3, auction.getItemId());
			stm.setInt(4, auction.getCount());
			stm.setInt(5, auction.getEnchant());
			stm.setInt(6, auction.getCostId());
			stm.setInt(7, auction.getCostCount());
			stm.setLong(8, auction.getStartingPrice());
			stm.setLong(9, auction.getCurrentBid());
			stm.setLong(10, auction.getBuyNowPrice());
			stm.setInt(11, auction.getBidderId());
			stm.setTimestamp(12, new Timestamp(auction.getStartTime().getTime()));
			stm.setTimestamp(13, new Timestamp(auction.getEndTime().getTime()));
			stm.setString(14, auction.getStatus().name().toLowerCase());
			if (partial)
			{
				stm.setInt(15, auction.isAllowPartial() ? 1 : 0);
				stm.setInt(16, auction.getOriginalCount());
			}
			stm.execute();
		}
		catch (Exception e)
		{
			log.warn("AuctionHouse: erro ao gravar o anúncio " + auction.getAuctionId(), e);
			return false;
		}

		bids.put(auction.getAuctionId(), new CopyOnWriteArrayList<>());
		auctions.put(auction.getAuctionId(), auction);
		return true;
	}

	/** Grava lance atual, licitante, fim e status. false = não gravou (nada mudou no banco). */
	public boolean updateAuction(AuctionHolder auction)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(
				"UPDATE auction_house_table SET current_bid=?, bidder_id=?, end_time=?, status=? WHERE auctionid=?"))
		{
			stm.setLong(1, auction.getCurrentBid());
			stm.setInt(2, auction.getBidderId());
			stm.setTimestamp(3, new Timestamp(auction.getEndTime().getTime()));
			stm.setString(4, auction.getStatus().name().toLowerCase());
			stm.setInt(5, auction.getAuctionId());
			int rows = stm.executeUpdate();
			if (rows != 1)
			{
				log.warn("AuctionHouse: leilão " + auction.getAuctionId() + " não atualizou no banco (rows=" + rows + ").");
				return false;
			}
			return true;
		}
		catch (Exception e)
		{
			log.warn("AuctionHouse: erro ao atualizar o leilão " + auction.getAuctionId(), e);
			return false;
		}
	}

	/** Compra parcial: grava o que resta (quantidade, preço inicial e compra imediata) com este status. */
	public boolean updateRemaining(AuctionHolder auction, AuctionHolder.AuctionStatus status)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(
				"UPDATE auction_house_table SET count=?, starting_price=?, buy_now_price=?, status=? WHERE auctionid=?"))
		{
			stm.setInt(1, auction.getCount());
			stm.setLong(2, auction.getStartingPrice());
			stm.setLong(3, auction.getBuyNowPrice());
			stm.setString(4, status.name().toLowerCase());
			stm.setInt(5, auction.getAuctionId());
			int rows = stm.executeUpdate();
			if (rows != 1)
				log.warn("AuctionHouse: restante do anúncio " + auction.getAuctionId() + " não gravou (rows=" + rows + ").");
			return rows == 1;
		}
		catch (Exception e)
		{
			log.warn("AuctionHouse: erro ao gravar o restante do anúncio " + auction.getAuctionId(), e);
			return false;
		}
	}

	/** Uma compra parcial no histórico (uma linha por compra) e na média de preço. */
	public void addSaleHistory(AuctionHolder auction, int buyerId, long qty, long price)
	{
		insertHistory(auction, buyerId, (int) qty, price, "sold");
		// fora do banco: a média de preço vale mesmo se o INSERT falhar
		AuctionPriceStats.record(auction.getItemId(), auction.getEnchant(), auction.getCostId(), qty, price);
	}

	/** Linha do histórico e média de preço do anúncio encerrado (vendido ou não). */
	public void saveHistory(AuctionHolder auction)
	{
		boolean sold = auction.getBidderId() > 0;
		insertHistory(auction, sold ? auction.getBidderId() : 0, auction.getCount(), auction.getCurrentBid(), sold ? "sold" : "expired");
		if (sold)
			AuctionPriceStats.record(auction.getItemId(), auction.getEnchant(), auction.getCostId(), auction.getCount(), auction.getCurrentBid());
	}

	private void insertHistory(AuctionHolder auction, int buyerId, int count, long price, String status)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(
				"INSERT INTO auction_house_history (auction_id, seller_id, buyer_id, item_id, item_count, final_price, ended_time, status) "
					+ "VALUES (?,?,?,?,?,?,?,?)"))
		{
			stm.setInt(1, auction.getAuctionId());
			stm.setInt(2, auction.getSellerId());
			stm.setInt(3, buyerId);
			stm.setInt(4, auction.getItemId());
			stm.setInt(5, count);
			stm.setLong(6, price);
			stm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			stm.setString(8, status);
			stm.execute();
		}
		catch (Exception e)
		{
			log.warn("AuctionHouse: erro ao gravar o histórico do leilão " + auction.getAuctionId(), e);
		}
	}

	public void addBid(AuctionBid bid)
	{
		bids.computeIfAbsent(bid.getAuctionId(), k -> new CopyOnWriteArrayList<>()).add(bid);

		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(
				"INSERT INTO auction_house_bids (auction_id, bidder_id, bid_amount, bid_time, is_cancelled) VALUES (?,?,?,?,0)"))
		{
			stm.setInt(1, bid.getAuctionId());
			stm.setInt(2, bid.getBidderId());
			stm.setLong(3, bid.getBidAmount());
			stm.setTimestamp(4, new Timestamp(bid.getBidTime().getTime()));
			stm.execute();
		}
		catch (Exception e)
		{
			log.warn("AuctionHouse: erro ao gravar o lance no leilão " + bid.getAuctionId(), e);
		}
	}

	// =====================================================================
	// Cancelar / encerrar
	// =====================================================================

	/**
	 * Grava CANCELLED e só depois devolve item (ao vendedor) e lance (ao licitante). false = o banco não gravou e
	 * nada foi devolvido: o anúncio continua ativo. Chamar dentro de synchronized (auction).
	 */
	public boolean cancelAuction(AuctionHolder auction)
	{
		if (!saveStatus(auction, AuctionHolder.AuctionStatus.CANCELLED))
		{
			log.warn("AuctionHouse: cancelamento do leilão " + auction.getAuctionId() + " não gravou no banco; nada foi devolvido.");
			return false;
		}

		// venda parcial: só o que não foi vendido volta
		AuctionDelivery.deliverItem(AuctionDelivery.K_CANCEL_ITEM, auction.getSellerId(), auction.getItemId(), auction.getCount(),
			auction.getEnchant(), AuctionDelivery.label(auction, auction.getCount()),
			"Seu anúncio foi cancelado e o item voltou para você.",
			"Auction House: leilão " + auction.getAuctionId() + " cancelado (item devolvido)");
		AuctionNotifications.notifyAuctionCancelled(World.getPlayer(auction.getSellerId()), auction, AuctionNotifications.CancelType.SELLER);

		if (auction.getBidderId() > 0 && auction.getCurrentBid() > 0)
		{
			AuctionDelivery.deliverCurrency(AuctionDelivery.K_CANCEL_BID, auction.getBidderId(), auction.getCostId(), auction.getCurrentBid(),
				AuctionDelivery.label(auction, auction.getCount()),
				"O vendedor cancelou o anúncio. Seu lance de "
					+ AuctionDelivery.money(auction.getCurrentBid(), auction.getCostId()) + " voltou para você.",
				"Auction House: leilão " + auction.getAuctionId() + " cancelado (lance devolvido)");
			AuctionNotifications.notifyAuctionCancelled(World.getPlayer(auction.getBidderId()), auction, AuctionNotifications.CancelType.BIDDER);
		}

		auctions.remove(auction.getAuctionId());
		bids.remove(auction.getAuctionId());
		return true;
	}

	/**
	 * Grava ENDED (com o lance/comprador atuais). Vem ANTES de qualquer entrega: se o banco não gravar, nada é
	 * entregue e o anúncio continua ativo.
	 */
	public boolean markEnded(AuctionHolder auction)
	{
		return saveStatus(auction, AuctionHolder.AuctionStatus.ENDED);
	}

	/** Troca o status e grava; se não gravar, volta ao anterior. */
	private boolean saveStatus(AuctionHolder auction, AuctionHolder.AuctionStatus status)
	{
		AuctionHolder.AuctionStatus before = auction.getStatus();
		auction.setStatus(status);
		if (updateAuction(auction))
			return true;
		auction.setStatus(before);
		return false;
	}

	/**
	 * Encerra o leilão se o tempo acabou e avisa quem está com ele aberto. Mesmo cadeado do cancelar / lance /
	 * compra. false = nada a encerrar (cancelado ou comprado enquanto esperava, lance de última hora estendeu o fim)
	 * ou o banco não gravou (tenta de novo na próxima verificação).
	 */
	public boolean endIfExpired(AuctionHolder auction)
	{
		synchronized (auction)
		{
			if (!auction.isActive() || auction.getTimeRemaining() > 0)
				return false;
			if (!processAuctionEnd(auction))
				return false;
		}

		// quem está com a janela "Dar lance" aberta vê "encerrado" na hora
		AuctionHouseService.notifyEnded(auction, "Este leilão foi encerrado.", 0);
		return true;
	}

	private boolean processAuctionEnd(AuctionHolder auction)
	{
		if (!markEnded(auction))
		{
			log.warn("AuctionHouse: leilão " + auction.getAuctionId() + " não gravou ENDED no banco; tento de novo na próxima verificação.");
			return false;
		}

		String label = AuctionDelivery.label(auction, auction.getCount());
		if (auction.getCurrentBid() > 0 && auction.getBidderId() > 0)
		{
			long price = auction.getCurrentBid();
			long tax = AuctionHouseService.taxFor(price);
			long net = Math.max(0L, price - tax);

			log.info("AuctionHouse: leilão " + auction.getAuctionId() + " vendido para " + playerName(auction.getBidderId()) + " por " + price
					+ " (item " + auction.getItemId() + " x" + auction.getCount() + ", vendedor " + auction.getSellerId()
					+ " recebe " + net + ", taxa " + tax + ").");

			AuctionDelivery.deliverItem(AuctionDelivery.K_WON, auction.getBidderId(), auction.getItemId(), auction.getCount(),
				auction.getEnchant(), label,
				"Você venceu o leilão com o lance de " + AuctionDelivery.money(price, auction.getCostId()) + ".",
				"Auction House: leilão " + auction.getAuctionId() + " vencido");
			AuctionNotifications.notifyWonAuction(World.getPlayer(auction.getBidderId()), auction);

			AuctionDelivery.deliverCurrency(AuctionDelivery.K_SOLD, auction.getSellerId(), auction.getCostId(), net, label,
				"Vendido por " + AuctionDelivery.money(price, auction.getCostId())
					+ (tax > 0 ? ". Taxa: " + AuctionDelivery.money(tax, auction.getCostId()) : "")
					+ ". Você recebe " + AuctionDelivery.money(net, auction.getCostId()) + ".",
				"Auction House: venda do leilão " + auction.getAuctionId());
			AuctionNotifications.notifySellerPaid(World.getPlayer(auction.getSellerId()), auction);
		}
		else
		{
			log.info("AuctionHouse: leilão " + auction.getAuctionId() + " acabou sem venda (item " + auction.getItemId() + " x"
					+ auction.getCount() + " devolvido ao vendedor " + auction.getSellerId() + ").");

			// venda parcial: o que restou
			AuctionDelivery.deliverItem(AuctionDelivery.K_NO_SALE, auction.getSellerId(), auction.getItemId(), auction.getCount(),
				auction.getEnchant(), label,
				"O prazo do anúncio acabou sem venda e o item voltou para você.",
				"Auction House: leilão " + auction.getAuctionId() + " sem venda (item devolvido)");
			AuctionNotifications.notifyItemReturned(World.getPlayer(auction.getSellerId()), auction);
		}

		saveHistory(auction);
		return true;
	}

	/** Aviso ao vendedor quando o anúncio acaba sem vender tudo. */
	static String returnedMessage(AuctionHolder auction)
	{
		int sold = auction.getSoldCount();
		if (sold <= 0)
			return "O prazo acabou sem venda: " + AuctionDelivery.label(auction, auction.getCount()) + " foi devolvido "
					+ AuctionDelivery.where() + ".";

		return "Anúncio encerrado: vendidas " + AuctionHouseService.fmt(sold) + " de "
			+ AuctionHouseService.fmt(auction.getOriginalCount()) + ". As "
			+ AuctionHouseService.fmt(auction.getCount()) + " restantes voltaram " + AuctionDelivery.where() + ".";
	}

	private static String playerName(int objId)
	{
		Player p = World.getPlayer(objId);
		if (p != null)
			return p.getName();
		String name = CharacterDAO.getInstance().getNameByObjectId(objId);
		return name != null && !name.isEmpty() ? name : "objId " + objId;
	}

	// =====================================================================
	// Consulta
	// =====================================================================

	public AuctionHolder getAuction(int auctionId)
	{
		return auctions.get(auctionId);
	}

	public Map<Integer, AuctionHolder> getAuctions()
	{
		return auctions;
	}

	public List<AuctionBid> getBids(int auctionId)
	{
		List<AuctionBid> list = bids.get(auctionId);
		return list != null ? list : Collections.emptyList();
	}

	/** Próximo id livre (maior do banco + 1). Chamado dentro do cadeado da criação (Interface.AUC_CREATE_LOCK). */
	public synchronized int getNextAuctionId()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement("SELECT MAX(auctionid) AS max_id FROM auction_house_table");
			ResultSet rset = stm.executeQuery())
		{
			if (rset.next())
				maxId = Math.max(maxId, rset.getInt("max_id"));
		}
		catch (Exception e)
		{
			log.warn("AuctionHouse: erro ao ler o próximo id de leilão", e);
		}
		return ++maxId;
	}

	// =====================================================================
	// Tarefas
	// =====================================================================

	private void startAuctionExpirationTask()
	{
		// encerra os leilões cujo tempo acabou
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			for (AuctionHolder auction : auctions.values().toArray(new AuctionHolder[0]))
			{
				if (!auction.isActive() || auction.getTimeRemaining() > 0)
					continue;
				try
				{
					endIfExpired(auction);
				}
				catch (Exception e)
				{
					log.warn("AuctionHouse: erro ao encerrar o leilão " + auction.getAuctionId(), e);
				}
			}
		}, EXPIRE_CHECK_MS, EXPIRE_CHECK_MS);

		// tira da memória os já encerrados (compra imediata, venda parcial esgotada, fim do tempo)
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			try
			{
				for (AuctionHolder auction : auctions.values().toArray(new AuctionHolder[0]))
					if (auction.isEnded())
					{
						auctions.remove(auction.getAuctionId(), auction);
						bids.remove(auction.getAuctionId());
					}
			}
			catch (Exception e)
			{
				log.warn("AuctionHouse: erro na limpeza de leilões encerrados", e);
			}
		}, CLEANUP_MS, CLEANUP_MS);
	}

	private static class SingletonHolder
	{
		protected static final AuctionTableCommunity _instance = new AuctionTableCommunity();
	}
}
