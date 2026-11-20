package services.randomcraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.templates.item.ItemTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuração do Random Craft: game/config/custom/randomcraft.properties (recarrega com //reload_config).
 * Arquivo ausente = valores padrão (um pool de exemplo).
 */
public final class RandomCraftConfig
{
	private static final Logger _log = LoggerFactory.getLogger(RandomCraftConfig.class);

	public static final String FILE = "config/custom/randomcraft.properties";

	/** Casas da janela (RandomSlotGroup00..04). */
	public static final int SLOTS = 5;
	public static final int MAX_GRADE = 4;
	public static final int MAX_POOL = 3000;
	public static final int MAX_EXTRACT = 100;

	/** Uma recompensa possível. grade 0..4 (3 e 4 brilham na janela). */
	public static final class Reward
	{
		public final int id;
		public final long count;
		public final int grade;
		public final int weight;
		public final int enchant;

		public Reward(int id, long count, int grade, int weight, int enchant)
		{
			this.id = id;
			this.count = count;
			this.grade = grade;
			this.weight = weight;
			this.enchant = enchant;
		}
	}

	/** Item que vira experiência de craft. */
	public static final class Extract
	{
		public final int id;
		public final long exp;
		public final long adena;

		public Extract(int id, long exp, long adena)
		{
			this.id = id;
			this.exp = exp;
			this.adena = adena;
		}
	}

	public static boolean ENABLED = true;

	/** Experiência para virar 1 Craft Point, e teto de pontos. */
	public static int MAX_EXP = 1000;
	public static int MAX_POINTS = 50;

	/** Contador de travas por casa até a próxima criação: +1 a cada travar e +1 a cada atualização com a casa travada. */
	public static int MAX_LOCKS = 20;
	/** Quantas casas podem ficar travadas ao mesmo tempo. */
	public static int MAX_LOCKED_SLOTS = 3;

	/** Atualizar a lista: pontos + item (id 0 = sem item). */
	public static int REFRESH_POINTS = 1;
	public static int REFRESH_ITEM_ID = 57;
	public static long REFRESH_ITEM_COUNT = 10000L;

	/** Travar uma casa: item; custo = base + passo x vezes que a casa já foi travada. */
	public static int LOCK_ITEM_ID = 57;
	public static long LOCK_ITEM_COUNT = 5000L;
	public static long LOCK_ITEM_STEP = 5000L;

	/** Criar: pontos + item. */
	public static int CREATE_POINTS = 0;
	public static int CREATE_ITEM_ID = 57;
	public static long CREATE_ITEM_COUNT = 50000L;

	public static boolean MAIL_WHEN_FULL = true;
	public static String MAIL_SENDER = "Random Craft";

	public static List<Reward> POOL = defaultPool();
	public static Map<Integer, Extract> EXTRACT = defaultExtract();

	private RandomCraftConfig()
	{
	}

	public static void load()
	{
		try
		{
			ExProperties p = Config.load(FILE);
			ENABLED = p.getProperty("RandomCraftEnabled", true);
			MAX_EXP = Math.max(1, p.getProperty("MaxExp", 1000));
			MAX_POINTS = Math.max(1, p.getProperty("MaxPoints", 50));
			MAX_LOCKS = Math.max(1, p.getProperty("MaxLocks", 20));
			MAX_LOCKED_SLOTS = Math.max(1, Math.min(SLOTS, p.getProperty("MaxLockedSlots", 3)));
			REFRESH_POINTS = Math.max(0, p.getProperty("RefreshPoints", 1));
			REFRESH_ITEM_ID = item("RefreshItemId", p.getProperty("RefreshItemId", 57));
			REFRESH_ITEM_COUNT = Math.max(1L, p.getProperty("RefreshItemCount", 10000L));
			LOCK_ITEM_ID = item("LockItemId", p.getProperty("LockItemId", 57));
			LOCK_ITEM_COUNT = Math.max(1L, p.getProperty("LockItemCount", 5000L));
			LOCK_ITEM_STEP = Math.max(0L, p.getProperty("LockItemStep", 5000L));
			CREATE_POINTS = Math.max(0, p.getProperty("CreatePoints", 0));
			CREATE_ITEM_ID = item("CreateItemId", p.getProperty("CreateItemId", 57));
			CREATE_ITEM_COUNT = Math.max(1L, p.getProperty("CreateItemCount", 50000L));
			MAIL_WHEN_FULL = p.getProperty("RewardByMailWhenFull", true);
			MAIL_SENDER = p.getProperty("MailSender", "Random Craft").trim();
			if (MAIL_SENDER.isEmpty())
				MAIL_SENDER = "Random Craft";

			List<Reward> pool = new ArrayList<>();
			for (int i = 1; i <= MAX_POOL; i++)
			{
				String v = p.getProperty("Reward" + i, "").trim();
				if (v.isEmpty())
					continue;
				Reward r = parseReward(i, v);
				if (r != null)
					pool.add(r);
			}
			if (!p.getProperty("Reward" + (MAX_POOL + 1), "").trim().isEmpty())
				_log.warn("RandomCraft: o pool aceita RewardN até " + MAX_POOL + "; as entradas acima disso foram ignoradas.");
			if (pool.isEmpty())
			{
				_log.warn("RandomCraft: nenhum RewardN válido; usando o pool de exemplo.");
				pool = defaultPool();
			}
			POOL = pool;

			Map<Integer, Extract> ex = new LinkedHashMap<>();
			for (int i = 1; i <= MAX_EXTRACT; i++)
			{
				String v = p.getProperty("Extract" + i, "").trim();
				if (v.isEmpty())
					continue;
				Extract e = parseExtract(i, v);
				if (e != null)
					ex.put(e.id, e);
			}
			EXTRACT = ex;
		}
		catch (Exception e)
		{
			_log.warn("RandomCraft: erro lendo " + FILE + " (ficam os valores padrão): " + e.getMessage());
		}
	}

	/** Id 0 = sem item; id que não existe também vira 0 (com aviso). */
	private static int item(String key, int id)
	{
		if (id <= 0)
			return 0;
		if (ItemHolder.getInstance().getTemplate(id) == null)
		{
			_log.warn("RandomCraft: " + key + " " + id + " não existe; sem custo em item.");
			return 0;
		}
		return id;
	}

	/** "item, quantidade, grau(0-4), peso[, encantamento]" */
	static Reward parseReward(int n, String v)
	{
		try
		{
			String[] a = v.split(",");
			int id = Integer.parseInt(a[0].trim());
			long count = a.length > 1 ? Long.parseLong(a[1].trim()) : 1L;
			int grade = a.length > 2 ? Integer.parseInt(a[2].trim()) : 0;
			int weight = a.length > 3 ? Integer.parseInt(a[3].trim()) : 10;
			int ench = a.length > 4 ? Integer.parseInt(a[4].trim()) : 0;
			ItemTemplate t = ItemHolder.getInstance().getTemplate(id);
			if (t == null)
			{
				_log.warn("RandomCraft: Reward" + n + ": item " + id + " não existe (ignorado).");
				return null;
			}
			if (count <= 0 || weight <= 0)
			{
				_log.warn("RandomCraft: Reward" + n + ": quantidade e peso precisam ser maiores que 0 (ignorado).");
				return null;
			}
			if (count > Integer.MAX_VALUE)
				count = Integer.MAX_VALUE;
			if (!t.isStackable() && count > 8)
			{
				_log.warn("RandomCraft: Reward" + n + ": item " + id + " não empilha; quantidade limitada a 8.");
				count = 8;
			}
			if (grade < 0 || grade > MAX_GRADE)
			{
				_log.warn("RandomCraft: Reward" + n + ": grau " + grade + " fora de 0.." + MAX_GRADE + "; usando 0.");
				grade = 0;
			}
			if (ench < 0 || (ench > 0 && t.isStackable()))
				ench = 0;
			return new Reward(id, count, grade, weight, ench);
		}
		catch (Exception e)
		{
			_log.warn("RandomCraft: Reward" + n + " inválido (\"" + v + "\"): " + e.getMessage());
			return null;
		}
	}

	/** "item, experiência por unidade, adena por unidade" (só item que empilha) */
	static Extract parseExtract(int n, String v)
	{
		try
		{
			String[] a = v.split(",");
			int id = Integer.parseInt(a[0].trim());
			long exp = Long.parseLong(a[1].trim());
			long adena = a.length > 2 ? Long.parseLong(a[2].trim()) : 0L;
			ItemTemplate t = ItemHolder.getInstance().getTemplate(id);
			if (t == null)
			{
				_log.warn("RandomCraft: Extract" + n + ": item " + id + " não existe (ignorado).");
				return null;
			}
			if (!t.isStackable())
			{
				_log.warn("RandomCraft: Extract" + n + ": item " + id + " não empilha (só itens que empilham; ignorado).");
				return null;
			}
			if (exp <= 0 || adena < 0)
			{
				_log.warn("RandomCraft: Extract" + n + ": experiência precisa ser maior que 0 (ignorado).");
				return null;
			}
			return new Extract(id, exp, adena);
		}
		catch (Exception e)
		{
			_log.warn("RandomCraft: Extract" + n + " inválido (\"" + v + "\"): " + e.getMessage());
			return null;
		}
	}

	static List<Reward> defaultPool()
	{
		List<Reward> l = new ArrayList<>();
		l.add(new Reward(57, 50000L, 0, 40, 0));
		l.add(new Reward(1539, 20L, 0, 30, 0));
		l.add(new Reward(955, 1L, 1, 20, 0));
		l.add(new Reward(956, 1L, 1, 20, 0));
		l.add(new Reward(951, 1L, 2, 10, 0));
		l.add(new Reward(947, 1L, 3, 4, 0));
		l.add(new Reward(729, 1L, 4, 1, 0));
		return l;
	}

	static Map<Integer, Extract> defaultExtract()
	{
		Map<Integer, Extract> m = new LinkedHashMap<>();
		long[][] d = { { 1458, 5 }, { 1459, 10 }, { 1460, 20 }, { 1461, 40 }, { 1462, 80 } };
		for (long[] x : d)
			m.put((int) x[0], new Extract((int) x[0], x[1], 100L));
		return m;
	}
}
