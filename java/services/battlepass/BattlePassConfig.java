package services.battlepass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.templates.item.ItemTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuração do Battle Pass: game/config/custom/battlepass.properties (recarrega com //reload_config).
 *
 * Fica nos scripts: recarrega junto com os scripts ou com ".passreload" (GM).
 * Arquivo ausente = valores padrão abaixo (10 degraus de exemplo).
 */
public final class BattlePassConfig
{
	private static final Logger _log = LoggerFactory.getLogger(BattlePassConfig.class);

	public static final String FILE = "config/custom/battlepass.properties";

	/** Degraus por página na janela (as 10 colunas do L2PassWnd). */
	public static final int PAGE_SIZE = 10;
	/** Teto de degraus (os resgates ficam num bitmask de 63 bits). */
	public static final int MAX_STEPS = 60;
	/** Quantidade máxima de um item que não empilha numa recompensa. */
	public static final int MAX_UNSTACKABLE = 8;

	/** Um degrau: pontos para completar e as duas recompensas (id 0 = sem recompensa). */
	public static final class Step
	{
		public final int need;
		public final int freeId;
		public final long freeCount;
		public final int premId;
		public final long premCount;

		public Step(int need, int freeId, long freeCount, int premId, long premCount)
		{
			this.need = need;
			this.freeId = freeId;
			this.freeCount = freeCount;
			this.premId = premId;
			this.premCount = premCount;
		}
	}

	public static boolean ENABLED = true;

	/** Início da 1a temporada (ms) e duração em dias (0 = sem reset). */
	public static long SEASON_START = 0L;
	public static int SEASON_DAYS = 30;

	/** Pontos por morte. */
	public static int POINTS_MONSTER = 1;
	public static int POINTS_CHAMPION = 3;
	public static int POINTS_RAID = 50;
	public static int POINTS_BOSS = 100;

	/** Monstro com nível abaixo de (nível do jogador - MAX_LEVEL_DIFF) não dá pontos. 0 = sem limite. */
	public static int MAX_LEVEL_DIFF = 9;
	/** Nível mínimo do jogador para ganhar pontos. */
	public static int MIN_LEVEL = 1;

	/** Party: os membros perto do monstro ganham os mesmos pontos. */
	public static boolean PARTY_SHARE = true;
	public static int PARTY_RANGE = 1500;

	/** Premium: moeda (id) e quantidade. Id 0 = premium desligado. */
	public static int PREMIUM_ITEM_ID = 4037;
	public static long PREMIUM_ITEM_COUNT = 10L;

	/** Inventário sem espaço / peso: a recompensa vai pelo correio. */
	public static boolean MAIL_WHEN_FULL = true;
	public static String MAIL_SENDER = "Battle Pass";

	/** Aviso na tela ao completar um degrau. */
	public static boolean STEP_NOTIFY = true;

	/** Segundos entre gravações dos pontos no banco (resgate e premium gravam na hora). */
	public static int SAVE_INTERVAL = 60;

	public static List<Step> STEPS = defaults();

	private BattlePassConfig()
	{
	}

	public static void load()
	{
		try
		{
			ExProperties p = Config.load(FILE);
			ENABLED = p.getProperty("BattlePassEnabled", true);
			SEASON_START = parseDate(p.getProperty("SeasonStart", "2026-10-01 00:00").trim());
			SEASON_DAYS = Math.max(0, p.getProperty("SeasonDays", 30));
			POINTS_MONSTER = Math.max(0, p.getProperty("PointsMonster", 1));
			POINTS_CHAMPION = Math.max(0, p.getProperty("PointsChampion", 3));
			POINTS_RAID = Math.max(0, p.getProperty("PointsRaid", 50));
			POINTS_BOSS = Math.max(0, p.getProperty("PointsBoss", 100));
			MAX_LEVEL_DIFF = Math.max(0, p.getProperty("MaxLevelDiff", 9));
			MIN_LEVEL = Math.max(1, p.getProperty("MinLevel", 1));
			PARTY_SHARE = p.getProperty("PartyShare", true);
			PARTY_RANGE = Math.max(0, p.getProperty("PartyRange", 1500));
			PREMIUM_ITEM_ID = Math.max(0, p.getProperty("PremiumItemId", 4037));
			PREMIUM_ITEM_COUNT = Math.max(1L, p.getProperty("PremiumItemCount", 10L));
			if (PREMIUM_ITEM_ID > 0 && ItemHolder.getInstance().getTemplate(PREMIUM_ITEM_ID) == null)
			{
				_log.warn("BattlePass: PremiumItemId " + PREMIUM_ITEM_ID + " não existe; premium desligado.");
				PREMIUM_ITEM_ID = 0;
			}
			MAIL_WHEN_FULL = p.getProperty("RewardByMailWhenFull", true);
			MAIL_SENDER = p.getProperty("MailSender", "Battle Pass").trim();
			if (MAIL_SENDER.isEmpty())
				MAIL_SENDER = "Battle Pass";
			STEP_NOTIFY = p.getProperty("StepNotify", true);
			SAVE_INTERVAL = Math.max(10, p.getProperty("SaveInterval", 60));

			List<Step> steps = new ArrayList<>();
			for (int i = 1; i <= MAX_STEPS; i++)
			{
				String v = p.getProperty("Step" + i, "").trim();
				if (v.isEmpty())
					break;
				Step s = parseStep(i, v, steps.isEmpty() ? 0 : steps.get(steps.size() - 1).need);
				if (s == null)
					break;
				steps.add(s);
			}
			STEPS = steps.isEmpty() ? defaults() : steps;
		}
		catch (Exception e)
		{
			_log.warn("BattlePass: erro lendo " + FILE + " (ficam os valores padrão): " + e.getMessage());
		}
	}

	/** "pontos;itemGratis,quantidade;itemPremium,quantidade" */
	static Step parseStep(int n, String v, int prevNeed)
	{
		String[] a = v.split(";", -1);
		try
		{
			int need = Integer.parseInt(a[0].trim());
			if (need <= prevNeed)
			{
				_log.warn("BattlePass: Step" + n + " pede " + need + " pontos, não mais que o degrau anterior (" + prevNeed + "); usando " + (prevNeed + 1) + ".");
				need = prevNeed + 1;
			}
			long[] free = a.length > 1 ? reward(n, "grátis", a[1]) : new long[2];
			long[] prem = a.length > 2 ? reward(n, "premium", a[2]) : new long[2];
			return new Step(need, (int) free[0], free[1], (int) prem[0], prem[1]);
		}
		catch (Exception e)
		{
			_log.warn("BattlePass: Step" + n + " inválido (\"" + v + "\"): " + e.getMessage() + ". Degraus a partir dele ignorados.");
			return null;
		}
	}

	private static long[] reward(int n, String what, String s)
	{
		s = s.trim();
		if (s.isEmpty())
			return new long[2];
		String[] b = s.split(",");
		int id = Integer.parseInt(b[0].trim());
		long count = b.length > 1 ? Long.parseLong(b[1].trim()) : 1L;
		if (id <= 0 || count <= 0)
			return new long[2];
		ItemTemplate t = ItemHolder.getInstance().getTemplate(id);
		if (t == null)
		{
			_log.warn("BattlePass: Step" + n + " recompensa " + what + ": item " + id + " não existe (degrau fica sem essa recompensa).");
			return new long[2];
		}
		if (count > Integer.MAX_VALUE)
			count = Integer.MAX_VALUE;
		// item que não empilha: no máximo 8 (cabe num correio só se o inventário estiver cheio)
		if (!t.isStackable() && count > MAX_UNSTACKABLE)
		{
			_log.warn("BattlePass: Step" + n + " recompensa " + what + ": " + count + "x item " + id + " que não empilha; usando " + MAX_UNSTACKABLE + ".");
			count = MAX_UNSTACKABLE;
		}
		return new long[] { id, count };
	}

	static long parseDate(String s) throws Exception
	{
		String f = s.length() > 10 ? "yyyy-MM-dd HH:mm" : "yyyy-MM-dd";
		SimpleDateFormat df = new SimpleDateFormat(f);
		df.setLenient(false);
		return df.parse(s).getTime();
	}

	/** 10 degraus de exemplo: Adena no grátis, Adena maior no premium. */
	static List<Step> defaults()
	{
		List<Step> l = new ArrayList<>();
		int[] need = { 100, 250, 500, 800, 1200, 1700, 2300, 3000, 4000, 5000 };
		for (int i = 0; i < need.length; i++)
			l.add(new Step(need[i], 57, 10000L * (i + 1), 57, 30000L * (i + 1)));
		return l;
	}

	public static int maxPoints()
	{
		List<Step> s = STEPS;
		return s.isEmpty() ? 0 : s.get(s.size() - 1).need;
	}
}
