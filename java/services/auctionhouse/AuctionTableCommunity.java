package services.auctionhouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.s2c.InventoryUpdate;

public class AuctionTableCommunity
{
	private static final Logger log = LoggerFactory.getLogger(AuctionTableCommunity.class);

	private Map<Integer, AuctionHolder> auctions;
	private Map<Integer, List<AuctionBid>> bids;
	private int maxId;

	// Colunas da venda parcial (allow_partial, original_count) existem no
	// banco? Criadas sozinhas no load; se o ALTER falhar (sem permissão), o
	// leilão continua funcionando, só sem compra parcial.
	private volatile boolean partialColumns;

	public static AuctionTableCommunity getInstance()
	{
		return SingletonHolder._instance;
	}

	protected AuctionTableCommunity()
	{
		auctions = new ConcurrentHashMap<>();
		bids = new ConcurrentHashMap<>();
		maxId = 0;

		load();
		startAuctionExpirationTask();

	}

	private void load()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			ensurePartialColumns(con);

			PreparedStatement stm = con.prepareStatement(
					"SELECT * FROM auction_house_table WHERE status IN ('active', 'pending')");
			ResultSet rset = stm.executeQuery();

			Set<String> cols = columnNames(rset.getMetaData());
			boolean hasPartial  = cols.contains("allow_partial");
			boolean hasOriginal = cols.contains("original_count");

			while (rset.next())
			{
				int auctionId = rset.getInt("auctionid");
				int sellerId = rset.getInt("sellerid");
				int itemId = rset.getInt("itemid");
				int count = rset.getInt("count");
				int enchant = rset.getInt("enchant");
				int costId = rset.getInt("costid");
				int costCount = rset.getInt("costcount");

				long startingPrice = rset.getLong("starting_price");
				long currentBid = rset.getLong("current_bid");
				long buyNowPrice = rset.getLong("buy_now_price");

				int bidderId = rset.getInt("bidder_id");
				Date startTime = rset.getTimestamp("start_time");
				Date endTime = rset.getTimestamp("end_time");
				String statusStr = rset.getString("status");

				AuctionHolder auction = new AuctionHolder(
						auctionId, sellerId, itemId, count, enchant, costId, costCount,
						startingPrice, buyNowPrice, startTime, endTime);
				auction.setCurrentBid(currentBid);
				auction.setBidderId(bidderId);
				auction.setStatus(AuctionHolder.AuctionStatus.valueOf(statusStr.toUpperCase()));

				// anúncios antigos: sem compra parcial, quantidade anunciada = a atual
				if (hasPartial)
					auction.setAllowPartial(rset.getInt("allow_partial") == 1);
				if (hasOriginal)
					auction.setOriginalCount(rset.getInt("original_count"));

				auctions.put(auctionId, auction);

				if (auctionId > maxId)
					maxId = auctionId;
			}

			rset.close();
			stm.close();

			// Carregar lances
			stm = con.prepareStatement("SELECT * FROM auction_house_bids WHERE is_cancelled = 0 ORDER BY bid_time DESC");
			rset = stm.executeQuery();

			while (rset.next())
			{
				int auctionId = rset.getInt("auction_id");
				int bidderId = rset.getInt("bidder_id");
				long bidAmount = rset.getLong("bid_amount");
				Date bidTime = rset.getTimestamp("bid_time");

				AuctionBid bid = new AuctionBid(auctionId, bidderId, bidAmount, bidTime);

				if (!bids.containsKey(auctionId))
					bids.put(auctionId, new CopyOnWriteArrayList<>());
				bids.get(auctionId).add(bid);
			}

			rset.close();
			stm.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		log.info("AuctionTable: Loaded " + auctions.size() + " auctions.");
	}

	// ===== VENDA PARCIAL: COLUNAS NOVAS =====

	/** O banco tem as colunas da venda parcial (pode criar anúncio com compra parcial). */
	public boolean supportsPartial()
	{
		return partialColumns;
	}

	/**
	 * Cria allow_partial e original_count em auction_house_table se ainda não
	 * existem (mesmo conteúdo do auction_house_venda_parcial.sql). Anúncios
	 * antigos ficam com 0 nas duas: sem compra parcial e quantidade anunciada =
	 * a atual.
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
			log.warn("AuctionTable: não consegui criar as colunas da venda parcial ("
				+ e.getMessage() + "). Rode o auction_house_venda_parcial.sql; até lá a compra parcial fica desligada.");
		}

		checkHistoryKey(con);
	}

	/**
	 * Na venda parcial cada compra é uma linha do histórico com o mesmo
	 * auction_id. Se o histórico tiver uma chave única com auction_id, a
	 * partir da segunda compra o INSERT pode falhar (só fica no log): avisa
	 * uma vez aqui.
	 */
	private void checkHistoryKey(Connection con)
	{
		try (ResultSet rs = con.getMetaData().getIndexInfo(con.getCatalog(), null, "auction_house_history", true, false))
		{
			Map<String, List<String>> idx = new java.util.HashMap<>();
			while (rs.next())
			{
				String name = rs.getString("INDEX_NAME");
				String col  = rs.getString("COLUMN_NAME");
				if (name != null && col != null)
					idx.computeIfAbsent(name, k -> new ArrayList<>()).add(col.toLowerCase());
			}
			for (Map.Entry<String, List<String>> e : idx.entrySet())
			{
				if (e.getValue().contains("auction_id"))
					log.warn("AuctionTable: auction_house_history tem chave única com auction_id (" + e.getKey() + ": "
						+ e.getValue() + "). A venda parcial grava uma linha por compra: troque por uma chave própria "
						+ "(veja o auction_house_venda_parcial.sql).");
			}
		}
		catch (Exception e)
		{
			// só um aviso; sem ele tudo continua funcionando
		}
	}

	private static Set<String> columnNames(ResultSetMetaData md) throws java.sql.SQLException
	{
		Set<String> s = new HashSet<>();
		for (int i = 1; i <= md.getColumnCount(); i++)
			s.add(md.getColumnName(i).toLowerCase());
		return s;
	}

	/**
	 * Grava o anuncio novo e so entao o coloca na lista. false = o banco nao gravou: o anuncio nao existe (quem
	 * chamou devolve o item e a taxa); antes ficava so na memoria e sumia, com o item, no proximo reinicio.
	 */
	public boolean addAuction(AuctionHolder auction)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			boolean partial = partialColumns;
			PreparedStatement stm = con.prepareStatement(
					"INSERT INTO auction_house_table (auctionid, sellerid, itemid, count, enchant, " +
							"costid, costcount, starting_price, current_bid, buy_now_price, bidder_id, " +
					"start_time, end_time, status" + (partial ? ", allow_partial, original_count" : "") +
					") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?" + (partial ? ",?,?" : "") + ")");

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
			stm.close();
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

	public boolean updateAuction(AuctionHolder auction)
	{
		if (auction == null)
		{
			log.warn("Tentativa de atualizar leilão NULL.");
			return false;
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement stm = con.prepareStatement(
						"UPDATE auction_house_table " +
								"SET current_bid=?, bidder_id=?, end_time=?, status=? " +
						"WHERE auctionid=?"))
		{
			stm.setLong(1, auction.getCurrentBid());
			stm.setInt(2, auction.getBidderId());
			stm.setTimestamp(
					3,
					new Timestamp(auction.getEndTime().getTime())
					);
			stm.setString(
					4,
					auction.getStatus().name().toLowerCase()
					);
			stm.setInt(5, auction.getAuctionId());

			int rows = stm.executeUpdate();

			if (rows != 1)
			{
				log.warn(
						"Falha ao atualizar leilão. "
								+ "auctionId=" + auction.getAuctionId()
								+ " | rows=" + rows
						);

				return false;
			}

			auctions.put(auction.getAuctionId(), auction);

			return true;
		}
		catch (Exception e)
		{
			log.warn(
					"Erro ao atualizar leilão "
							+ auction.getAuctionId()
							+ ": "
							+ e.getMessage()
					);

			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Compra parcial: grava o que resta (quantidade, preço inicial e compra
	 * imediata, que acompanham a quantidade) e o status (ENDED quando esgota).
	 */
	public boolean updateRemaining(AuctionHolder auction)
	{
		return updateRemaining(auction, auction.getStatus());
	}

	/** Igual, gravando este status (a compra parcial só muda o status na memória depois que gravou). */
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
				log.warn("Falha ao gravar o restante do anúncio " + auction.getAuctionId() + " | rows=" + rows);
			return rows == 1;
		}
		catch (Exception e)
		{
			log.warn("Erro ao gravar o restante do anúncio " + auction.getAuctionId() + ": " + e.getMessage());
			return false;
		}
	}

	/**
	 * Uma compra parcial no histórico ("sold", com a quantidade e o valor
	 * dela) e no histórico de preço (média por unidade). Cada compra é uma
	 * linha; o anúncio continua com o que resta.
	 */
	public void addSaleHistory(AuctionHolder auction, int buyerId, long qty, long price)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(
				"INSERT INTO auction_house_history (auction_id, seller_id, buyer_id, item_id, item_count, " +
				"final_price, ended_time, status) VALUES (?,?,?,?,?,?,?,?)"))
		{
			stm.setInt(1, auction.getAuctionId());
			stm.setInt(2, auction.getSellerId());
			stm.setInt(3, buyerId);
			stm.setInt(4, auction.getItemId());
			stm.setInt(5, (int) qty);
			stm.setLong(6, price);
			stm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			stm.setString(8, "sold");
			stm.execute();
		}
		catch (Exception e)
		{
			log.warn("Erro ao salvar compra parcial no histórico (anúncio " + auction.getAuctionId() + "): " + e.getMessage());
		}

		// fora do try do banco: a média de preço vale mesmo se o INSERT falhar
		AuctionPriceStats.record(auction.getItemId(), auction.getEnchant(), auction.getCostId(), qty, price);
	}

	public void addBid(AuctionBid bid)
	{
		// CopyOnWriteArrayList: a lista e lida (My Bids, lista, detalhes) enquanto outro lance entra
		bids.computeIfAbsent(bid.getAuctionId(), k -> new CopyOnWriteArrayList<>()).add(bid);

		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement(
					"INSERT INTO auction_house_bids (auction_id, bidder_id, bid_amount, bid_time, is_cancelled) VALUES (?,?,?,?,0)");

			stm.setInt(1, bid.getAuctionId());
			stm.setInt(2, bid.getBidderId());
			stm.setLong(3, bid.getBidAmount());
			stm.setTimestamp(4, new Timestamp(bid.getBidTime().getTime()));

			stm.execute();
			stm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ===== CANCELAR LEILÃO =====
	/**
	 * Grava CANCELLED primeiro e so depois devolve item e lance. false = o banco nao gravou e nada foi devolvido
	 * (o anuncio continua ativo). Na ordem antiga, se a gravacao falhasse (ou o servidor caisse no meio), o
	 * anuncio voltava ativo no reinicio com o item ja devolvido: item e lance em dobro.
	 */
	public boolean cancelAuction(AuctionHolder auction)
	{
		AuctionHolder.AuctionStatus before = auction.getStatus();
		auction.setStatus(AuctionHolder.AuctionStatus.CANCELLED);
		if (!updateAuction(auction))
		{
			auction.setStatus(before);
			log.warn("AuctionHouse: cancelamento do leilão " + auction.getAuctionId() + " não gravou no banco; nada foi devolvido.");
			return false;
		}

		// ✅ Devolver ao vendedor o que resta (venda parcial: só o que não foi vendido)
		AuctionDelivery.deliverItem(AuctionDelivery.K_CANCEL_ITEM, auction.getSellerId(), auction.getItemId(), auction.getCount(),
			auction.getEnchant(), AuctionDelivery.label(auction, auction.getCount()),
			"Seu anúncio foi cancelado e o item voltou para você.",
			"Auction House: leilão " + auction.getAuctionId() + " cancelado (item devolvido)");

		Player seller = World.getPlayer(auction.getSellerId());
		if (seller != null && seller.isOnline())
		{
			// ✅ NOTIFICAR VENDEDOR
			try
			{
				AuctionNotifications.notifyAuctionCancelled(seller, auction, AuctionNotifications.CancelType.SELLER);
			}
			catch (Exception e)
			{
				log.warn("Aviso do leilão falhou: " + e.getMessage());
			}
		}

		// ✅ Devolver lance ao licitante
		if (auction.getBidderId() > 0 && auction.getCurrentBid() > 0)
		{
			AuctionDelivery.deliverCurrency(AuctionDelivery.K_CANCEL_BID, auction.getBidderId(), auction.getCostId(), auction.getCurrentBid(),
				AuctionDelivery.label(auction, auction.getCount()),
				"O vendedor cancelou o anúncio. Seu lance de "
					+ AuctionDelivery.money(auction.getCurrentBid(), auction.getCostId()) + " voltou para você.",
				"Auction House: leilão " + auction.getAuctionId() + " cancelado (lance devolvido)");

			Player bidder = World.getPlayer(auction.getBidderId());
			if (bidder != null && bidder.isOnline())
			{
				bidder.sendPacket(new InventoryUpdate());

				// ✅ NOTIFICAR LICITANTE
				try
				{
					AuctionNotifications.notifyAuctionCancelled(bidder, auction, AuctionNotifications.CancelType.BIDDER);
				}
				catch (Exception e)
				{
					log.warn("Aviso do leilão falhou: " + e.getMessage());
				}
			}
		}

		// ✅ REMOVER DO MAPA DE MEMÓRIA (✅ MELHOR PRÁTICA)
		auctions.remove(auction.getAuctionId());

		log.info("AuctionHouse: leilão " + auction.getAuctionId() + " cancelado.");
		return true;
	}

	/**
	 * Grava ENDED (com o lance/comprador atuais). Vem ANTES de qualquer entrega: se o banco nao gravar, nada e
	 * entregue e o anuncio continua ativo (a tarefa tenta de novo). Na ordem antiga uma falha aqui, ou o servidor
	 * cair entre a entrega e a gravacao, fazia o anuncio voltar ativo no reinicio e ser entregue de novo.
	 */
	public boolean markEnded(AuctionHolder auction)
	{
		AuctionHolder.AuctionStatus before = auction.getStatus();
		auction.setStatus(AuctionHolder.AuctionStatus.ENDED);
		if (updateAuction(auction))
			return true;
		auction.setStatus(before);
		return false;
	}

	/** ENDED no banco + histórico. false = não gravou (ver markEnded). */
	public boolean endAuction(AuctionHolder auction)
	{
		if (!markEnded(auction))
			return false;
		saveHistory(auction);
		return true;
	}

	/** Linha do histórico e média de preço do anúncio encerrado. */
	public void saveHistory(AuctionHolder auction)
	{
		// ✅ SALVAR NO HISTÓRICO
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement(
					"INSERT INTO auction_house_history (auction_id, seller_id, buyer_id, item_id, item_count, " +
					"final_price, ended_time, status) VALUES (?,?,?,?,?,?,?,?)");

			stm.setInt(1, auction.getAuctionId());
			stm.setInt(2, auction.getSellerId());
			stm.setInt(3, auction.getBidderId() > 0 ? auction.getBidderId() : 0);
			stm.setInt(4, auction.getItemId());
			stm.setInt(5, auction.getCount());
			stm.setLong(6, auction.getCurrentBid());
			stm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			stm.setString(8, auction.getBidderId() > 0 ? "sold" : "expired");

			stm.execute();
			stm.close();

		}
		catch (Exception e)
		{
			log.warn("Erro ao salvar no histórico: " + e.getMessage());
			e.printStackTrace();
		}

		// histórico de preço (média mostrada na criação e no tooltip); fora do
		// try do banco, igual à compra parcial: vale mesmo se o INSERT falhar
		if (auction.getBidderId() > 0)
			AuctionPriceStats.record(auction.getItemId(), auction.getEnchant(),
				auction.getCostId(), auction.getCount(), auction.getCurrentBid());
	}

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

	public int getNextAuctionId()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT MAX(auctionid) AS max_id FROM auction_house_table");
			ResultSet rset = stm.executeQuery();

			if (rset.next())
			{
				maxId = rset.getInt("max_id");
			}

			rset.close();
			stm.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		maxId++;
		return maxId;
	}

	// ===== START AUCTION EXPIRATION TASK (OTIMIZADO) =====
	// ===== START AUCTION EXPIRATION TASK =====
	private void startAuctionExpirationTask()
	{
		// =====================================================
		// VERIFICA LEILÕES EXPIRADOS A CADA 30 SEGUNDOS
		// =====================================================
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			try
			{
				/*
				 * Usamos uma cópia do mapa para evitar problemas caso
				 * processAuctionEnd() altere o mapa durante a execução.
				 */
				for (AuctionHolder auction : auctions.values().toArray(new AuctionHolder[0]))
				{
					if (auction == null)
						continue;

					if (!auction.isActive())
						continue;

					if (auction.getTimeRemaining() > 0)
						continue;

					try
				    {
				        // encerra (mesmo cadeado do cancelar / lance / compra) e avisa
				        // quem está com a janela "Dar lance" aberta
				        endIfExpired(auction);
					}
					catch (Exception e)
					{
						log.warn("AuctionHouse: erro ao encerrar o leilão " + auction.getAuctionId(), e);
					}
				}

			}
			catch (Exception e)
			{
				/*
				 * Proteção adicional da própria tarefa.
				 * Se algo inesperado acontecer fora do processamento
				 * individual do leilão, a thread não ficará perdida.
				 */
				log.warn("AuctionHouse: erro na verificação de leilões expirados", e);
			}

		}, 30000, 30000);


		// =====================================================
		// TASK DE LIMPEZA
		// Executada a cada 60 segundos
		// =====================================================
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			try
			{
				List<Integer> expiredIds = new ArrayList<>();

				for (AuctionHolder auction : auctions.values())
				{
					if (auction == null)
						continue;

					/*
					 * Só removemos do mapa os leilões que realmente
					 * foram processados e marcados como ENDED.
					 */
					if (auction.getStatus() == AuctionHolder.AuctionStatus.ENDED)
					{
						expiredIds.add(
								auction.getAuctionId()
								);
					}
				}

				for (Integer id : expiredIds)
				{
					auctions.remove(id);
					bids.remove(id);
				}
			}
			catch (Exception e)
			{
				log.warn("AuctionHouse: erro na limpeza de leilões encerrados", e);
			}

		}, 60000, 60000);

	}

	/**
	 * Encerra o leilão se o tempo acabou (entrega, paga, devolve, histórico) e
	 * avisa quem está com ele aberto. Mesmo cadeado do cancelar / lance /
	 * compra. Retorna false se não havia nada a encerrar: cancelado ou
	 * comprado enquanto esperava, ou um lance de última hora estendeu o fim.
	 */
	public boolean endIfExpired(AuctionHolder auction)
	{
		if (auction == null)
			return false;

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

	// ===== PROCESS AUCTION END (NÃO-ESTÁTICO) =====
	private boolean processAuctionEnd(AuctionHolder auction)
	{
		if (auction == null)
			return false;

		// grava ENDED antes de entregar qualquer coisa (ver markEnded)
		if (!markEnded(auction))
		{
			log.warn("AuctionHouse: leilão " + auction.getAuctionId() + " não gravou ENDED no banco; tento de novo na próxima verificação.");
			return false;
		}

		// ✅ Obter nome do vencedor (online ou offline)
		String winnerName = "Desconhecido";
		Player winner = World.getPlayer(auction.getBidderId());

		if (winner != null && winner.isOnline())
		{
			winnerName = winner.getName();
		}
		else
		{
			winnerName = getPlayerNameFromDB(auction.getBidderId());
		}

		if (auction.getCurrentBid() > 0 && auction.getBidderId() > 0)
		{
			// ✅ VENDEDOR RECEBE PAGAMENTO (USAR currentBid, NÃO buyNowPrice)
			long sellerAmount = auction.getCurrentBid();
			long tax = AuctionHouseService.taxFor(sellerAmount);   // mesma pasta, sem import
			sellerAmount = Math.max(0, sellerAmount - tax);

			log.info("AuctionHouse: leilão " + auction.getAuctionId() + " vendido para " + winnerName + " por " + auction.getCurrentBid()
					+ " (item " + auction.getItemId() + " x" + auction.getCount() + ", vendedor " + auction.getSellerId()
					+ " recebe " + sellerAmount + ", taxa " + tax + ").");

			// ✅ ENTREGAR ITEM AO VENCEDOR (online no inventário, offline pelo DelayedItems)
			AuctionDelivery.deliverItem(AuctionDelivery.K_WON, auction.getBidderId(), auction.getItemId(), auction.getCount(),
				auction.getEnchant(), AuctionDelivery.label(auction, auction.getCount()),
				"Você venceu o leilão com o lance de "
					+ AuctionDelivery.money(auction.getCurrentBid(), auction.getCostId()) + ".",
				"Auction House: leilão " + auction.getAuctionId() + " vencido");

			if (winner != null && winner.isOnline())
			{
				winner.sendMessage("Parabéns! Você venceu o leilão!");

				try
				{
					AuctionNotifications.notifyWonAuction(winner, auction);
				}
				catch (Exception e)
				{
					log.warn("Aviso do leilão falhou: " + e.getMessage());
				}
			}

			// ✅ PAGAR VENDEDOR (online no inventário, offline pelo DelayedItems)
			AuctionDelivery.deliverCurrency(AuctionDelivery.K_SOLD, auction.getSellerId(), auction.getCostId(), sellerAmount,
				AuctionDelivery.label(auction, auction.getCount()),
				"Vendido por " + AuctionDelivery.money(auction.getCurrentBid(), auction.getCostId())
					+ (tax > 0 ? ". Taxa: " + AuctionDelivery.money(tax, auction.getCostId()) : "")
					+ ". Você recebe " + AuctionDelivery.money(sellerAmount, auction.getCostId()) + ".",
				"Auction House: venda do leilão " + auction.getAuctionId());

			Player seller = World.getPlayer(auction.getSellerId());
			if (seller != null && seller.isOnline())
			{
				seller.sendPacket(new InventoryUpdate());

				seller.sendMessage("Seu item foi vendido por " + String.valueOf(auction.getCurrentBid()) + "!");
				if (tax > 0)
					seller.sendMessage("Taxa: " + String.valueOf(tax));
				
				//Notificar vendedor
				try
				{
					AuctionNotifications.notifySellerPaid(seller, auction);
				}
				catch (Exception e)
				{
					log.warn("Aviso do leilão falhou: " + e.getMessage());
				}
			}

			// ✅ ANÚNCIO
			//String currencyName = AuctionUtils.getCurrencyNameById(auction.getCostId());
			//Broadcast.gameAnnounceAuctionShop(winnerName + " venceu o leilão de " + 
			//ItemHolder.getInstance().getTemplate(auction.getItemId()).getName() + 
			//" por " + String.valueOf(auction.getCurrentBid()) + " " + currencyName);
		}
		else
		{
			log.info("AuctionHouse: leilão " + auction.getAuctionId() + " acabou sem venda (item " + auction.getItemId() + " x"
					+ auction.getCount() + " devolvido ao vendedor " + auction.getSellerId() + ").");

			// ✅ SEM LANCE / SEM VENDA - DEVOLVER AO VENDEDOR (venda parcial: o que restou)
			AuctionDelivery.deliverItem(AuctionDelivery.K_NO_SALE, auction.getSellerId(), auction.getItemId(), auction.getCount(),
				auction.getEnchant(), AuctionDelivery.label(auction, auction.getCount()),
				"O prazo do anúncio acabou sem venda e o item voltou para você.",
				"Auction House: leilão " + auction.getAuctionId() + " sem venda (item devolvido)");

			Player seller = World.getPlayer(auction.getSellerId());
			if (seller != null && seller.isOnline())
			{
				seller.sendMessage(returnedMessage(auction));

				//Notificar vendedor item voltou
				try
				{
					AuctionNotifications.notifyItemReturned(seller, auction);
				}
				catch (Exception e)
				{
					log.warn("Aviso do leilão falhou: " + e.getMessage());
				}
			}
		}

		saveHistory(auction);
		return true;
	}

	/** Aviso ao vendedor quando o anúncio acaba sem vender tudo. */
	static String returnedMessage(AuctionHolder auction)
	{
		int sold = auction.getSoldCount();
		if (sold <= 0)
			return "Seu item foi devolvido (não foi vendido).";

		return "Anúncio encerrado: vendidas " + AuctionHouseService.fmt(sold) + " de "
			+ AuctionHouseService.fmt(auction.getOriginalCount()) + ". As "
			+ AuctionHouseService.fmt(auction.getCount()) + " restantes voltaram para você.";
	}

	// ===== MÉTODO AUXILIAR: OBTER NOME DO JOGADOR =====
	private static String getPlayerNameFromDB(int playerId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement(
					"SELECT char_name FROM characters WHERE obj_Id = ?");
			stm.setInt(1, playerId);
			ResultSet rs = stm.executeQuery();

			if (rs.next())
			{
				String name = rs.getString("char_name");
				rs.close();
				stm.close();
				return name != null ? name : "Desconhecido";
			}

			rs.close();
			stm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return "Desconhecido";
	}

	public int getBidCount(int auctionId)
	{      
		List<AuctionBid> list = bids.get(auctionId);
		return list != null ? list.size() : 0;
	}

	private static class SingletonHolder
	{
		protected static final AuctionTableCommunity _instance = new AuctionTableCommunity();
	}
}