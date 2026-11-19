package services.auctionhouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Histórico de preço do leilão: as últimas vendas de cada item, separadas por
 * item + enchant + moeda (um +0 e um +10 não têm o mesmo preço, e Adena e
 * Gold Bar não se misturam).
 *
 * Fica tudo em memória: lido uma vez do banco (auction_house_history, com o
 * enchant e a moeda vindos do auction_house_table pelo auctionid) e
 * atualizado a cada venda (AuctionTableCommunity.endAuction → record). A lista
 * e o tooltip consultam a média e a última venda sem tocar no banco.
 */
public final class AuctionPriceStats
{
	private static final Logger _log = LoggerFactory.getLogger(AuctionPriceStats.class);

	/** Quantas vendas recentes de cada item entram na média. */
	public static final int KEEP = 10;

	/** Com pelo menos estas vendas, a maior e a menor ficam fora da média. */
	private static final int TRIM_FROM = 5;

	// item:enchant:moeda → preço por unidade de cada venda, a mais recente primeiro
	private static final Map<String, Deque<Double>> SALES = new ConcurrentHashMap<>();
	// item:enchant:moeda → última venda {valor total, quantidade, quando (ms)}
	private static final Map<String, long[]> LAST = new ConcurrentHashMap<>();
	private static final Object LOAD_LOCK = new Object();
	private static volatile boolean _loaded = false;

	private AuctionPriceStats()
	{
	}

	private static String key(int itemId, int enchant, int costId)
	{
		return itemId + ":" + enchant + ":" + costId;
	}

	/** Lê o histórico do banco (só na primeira vez). */
	public static void ensureLoaded()
	{
		if (_loaded)
			return;

		synchronized (LOAD_LOCK)
		{
			if (_loaded)
				return;
			load();
			_loaded = true;
		}
	}

	private static void load()
	{
		int rows = 0;

		try (Connection con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement st = con.prepareStatement(
				"SELECT h.item_id, t.enchant, t.costid, h.item_count, h.final_price, h.ended_time "
				+ "FROM auction_house_history h "
				+ "JOIN auction_house_table t ON t.auctionid = h.auction_id "
				+ "WHERE h.status = 'sold' AND h.final_price > 0 AND h.item_count > 0 "
				+ "ORDER BY h.ended_time DESC LIMIT 20000");
			ResultSet rs = st.executeQuery())
		{
			while (rs.next())
			{
				String k = key(rs.getInt(1), rs.getInt(2), rs.getInt(3));
				Deque<Double> d = SALES.computeIfAbsent(k, x -> new ArrayDeque<>());

				// vem da mais recente para a mais antiga: guarda só as KEEP primeiras
				if (d.size() < KEEP)
					d.addLast(rs.getLong(5) / (double) rs.getLong(4));

				// a primeira de cada item é a última venda
				if (!LAST.containsKey(k))
				{
					Timestamp ts = rs.getTimestamp(6);
					LAST.put(k, new long[] { rs.getLong(5), rs.getLong(4), ts != null ? ts.getTime() : 0L });
				}
				rows++;
			}
		}
		catch (Exception e)
		{
			_log.warn("AuctionHouse: não foi possível ler o histórico de preços: " + e.getMessage());
		}

		_log.info("AuctionHouse: histórico de preços carregado (" + rows + " vendas, "
			+ SALES.size() + " itens diferentes).");
	}

	/**
	 * Uma venda acabou de ser gravada no auction_house_history.
	 * price = valor total pago; count = quantidade do leilão.
	 */
	public static void record(int itemId, int enchant, int costId, long count, long price)
	{
		if (count <= 0 || price <= 0)
			return;

		// Primeira consulta ao histórico: a venda acabou de ser gravada no banco
		// e entra junto com o resto na leitura; não precisa somar de novo.
		if (!_loaded)
		{
			ensureLoaded();
			return;
		}

		String k = key(itemId, enchant, costId);
		Deque<Double> d = SALES.computeIfAbsent(k, x -> new ArrayDeque<>());
		synchronized (d)
		{
			d.addFirst(price / (double) count);
			while (d.size() > KEEP)
				d.removeLast();
		}
		LAST.put(k, new long[] { price, count, System.currentTimeMillis() });
	}

	/**
	 * Última venda deste item (mesmo enchant e moeda).
	 *
	 * @return {valor total pago, quantidade, segundos desde a venda};
	 *         {0, 0, 0} = nunca vendido
	 */
	public static long[] lastSale(int itemId, int enchant, int costId)
	{
		ensureLoaded();

		long[] l = LAST.get(key(itemId, enchant, costId));
		if (l == null || l[0] <= 0 || l[2] <= 0)
			return new long[] { 0L, 0L, 0L };

		long ago = (System.currentTimeMillis() - l[2]) / 1000L;
		if (ago < 0)
			ago = 0;
		return new long[] { l[0], l[1], ago };
	}

	/**
	 * Média das últimas vendas para "count" unidades deste item.
	 *
	 * @return {valor total para count unidades, quantas vendas entraram na conta};
	 *         {0, 0} = sem vendas registradas
	 */
	public static long[] average(int itemId, int enchant, int costId, long count)
	{
		ensureLoaded();

		Deque<Double> d = SALES.get(key(itemId, enchant, costId));
		if (d == null || count <= 0)
			return new long[] { 0L, 0L };

		List<Double> v;
		synchronized (d)
		{
			v = new ArrayList<>(d);
		}

		int n = v.size();
		if (n == 0)
			return new long[] { 0L, 0L };

		// Com 5 vendas ou mais, a maior e a menor ficam de fora: uma venda
		// "de amigo" (1 Adena) ou um exagero não puxa a média
		Collections.sort(v);
		int from = 0;
		int to = n;
		if (n >= TRIM_FROM)
		{
			from = 1;
			to = n - 1;
		}

		double sum = 0.0;
		for (int i = from; i < to; i++)
			sum += v.get(i);
		double unit = sum / (to - from);

		long total = Math.round(unit * count);
		if (total < 1L)
			total = 1L;
		if (total > Integer.MAX_VALUE)
			total = Integer.MAX_VALUE;

		return new long[] { total, n };
	}
}
