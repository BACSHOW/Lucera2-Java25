package l2.gameserver.model.entity.oly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlyController {
	private static final Logger _log = LoggerFactory.getLogger(OlyController.class);
	private static final SimpleDateFormat RV = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
	private static OlyController RW = null;
	private int RX;
	private boolean RY = false;
	private boolean RZ = false;
	private ScheduledFuture<?> Sa;
	private ScheduledFuture<?> Sb;
	private ScheduledFuture<?> Sc;
	private ScheduledFuture<?>[][] Sd;
	private ScheduledFuture<?>[] Se;
	private long Sf;
	private long Sg;
	private long Sh;
	private long[] Si;
	private int Sj;
	private long[][] Sk;
	private int Sl;
	private int Sm;
	private static final String Sn = "oly_season_id";
	private static final String So = "oly_season_calc";
	private static final String Sp = "SELECT `season_id`,`season_start_time`,`season_end_time`,`nominate_start`,`b_idx`,`b_s0`,`b_s1`,`b_s2`,`b_s3`,`c_idx`,`c_s0`,`c_e0`,`c_s1`,`c_e1`,`c_s2`,`c_e2`,`c_s3`,`c_e3`,`c_s4`,`c_e4`,`c_s5`,`c_e5`,`c_s6`,`c_e6`,`c_s7`,`c_e7`,`c_s8`,`c_e8`,`c_s9`,`c_e9`,`c_s10`,`c_e10`,`c_s11`,`c_e11`,`c_s12`,`c_e12`,`c_s13`,`c_e13`,`c_s14`,`c_e14`,`c_s15`,`c_e15`,`c_s16`,`c_e16`,`c_s17`,`c_e17`,`c_s18`,`c_e18`,`c_s19`,`c_e19`,`c_s20`,`c_e20`,`c_s21`,`c_e21`,`c_s22`,`c_e22`,`c_s23`,`c_e23`,`c_s24`,`c_e24`,`c_s25`,`c_e25`,`c_s26`,`c_e26`,`c_s27`,`c_e27`,`c_s28`,`c_e28`,`c_s29`,`c_e29`,`c_s30`,`c_e30` FROM `oly_season` WHERE `season_id` = ?";
	private static final String Sq = "REPLACE INTO `oly_season`(`season_id`,`season_start_time`,`season_end_time`,`nominate_start`,`b_idx`,`b_s0`,`b_s1`,`b_s2`,`b_s3`,`c_idx`,`c_s0`,`c_e0`,`c_s1`,`c_e1`,`c_s2`,`c_e2`,`c_s3`,`c_e3`,`c_s4`,`c_e4`,`c_s5`,`c_e5`,`c_s6`,`c_e6`,`c_s7`,`c_e7`,`c_s8`,`c_e8`,`c_s9`,`c_e9`,`c_s10`,`c_e10`,`c_s11`,`c_e11`,`c_s12`,`c_e12`,`c_s13`,`c_e13`,`c_s14`,`c_e14`,`c_s15`,`c_e15`,`c_s16`,`c_e16`,`c_s17`,`c_e17`,`c_s18`,`c_e18`,`c_s19`,`c_e19`,`c_s20`,`c_e20`,`c_s21`,`c_e21`,`c_s22`,`c_e22`,`c_s23`,`c_e23`,`c_s24`,`c_e24`,`c_s25`,`c_e25`,`c_s26`,`c_e26`,`c_s27`,`c_e27`,`c_s28`,`c_e28`,`c_s29`,`c_e29`,`c_s30`,`c_e30`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String Sr = "@OlyPartCnt";
	private static final String Ss = "oly_chero_season";
	private int St = -1;

	public static final OlyController getInstance() {
		if (RW == null) {
			RW = new OlyController();
		}

		return RW;
	}

	private OlyController() {
		this.Sk = new long[31][];
		this.Si = new long[4];
		this.Sd = new ScheduledFuture[this.Sk.length][];
		this.Se = new ScheduledFuture[this.Si.length];
		this.load();
		this.schedule();
	}

	public synchronized void load() {
		this.RX = ServerVariables.getInt("oly_season_id", 0);
		this.RY = ServerVariables.getBool("oly_season_calc", false);
		Connection var1 = null;
		PreparedStatement var2 = null;
		ResultSet var3 = null;

		try {
			var1 = DatabaseFactory.getInstance().getConnection();
			var2 = var1.prepareStatement(
					"SELECT `season_id`,`season_start_time`,`season_end_time`,`nominate_start`,`b_idx`,`b_s0`,`b_s1`,`b_s2`,`b_s3`,`c_idx`,`c_s0`,`c_e0`,`c_s1`,`c_e1`,`c_s2`,`c_e2`,`c_s3`,`c_e3`,`c_s4`,`c_e4`,`c_s5`,`c_e5`,`c_s6`,`c_e6`,`c_s7`,`c_e7`,`c_s8`,`c_e8`,`c_s9`,`c_e9`,`c_s10`,`c_e10`,`c_s11`,`c_e11`,`c_s12`,`c_e12`,`c_s13`,`c_e13`,`c_s14`,`c_e14`,`c_s15`,`c_e15`,`c_s16`,`c_e16`,`c_s17`,`c_e17`,`c_s18`,`c_e18`,`c_s19`,`c_e19`,`c_s20`,`c_e20`,`c_s21`,`c_e21`,`c_s22`,`c_e22`,`c_s23`,`c_e23`,`c_s24`,`c_e24`,`c_s25`,`c_e25`,`c_s26`,`c_e26`,`c_s27`,`c_e27`,`c_s28`,`c_e28`,`c_s29`,`c_e29`,`c_s30`,`c_e30` FROM `oly_season` WHERE `season_id` = ?");
			var2.setInt(1, this.RX);
			var3 = var2.executeQuery();
			if (var3.next()) {
				this.Sf = var3.getLong("season_start_time");
				this.Sg = var3.getLong("season_end_time");
				this.Sh = var3.getLong("nominate_start");
				this.Sj = var3.getInt("b_idx");
				this.Si[0] = var3.getLong("b_s0");
				this.Si[1] = var3.getLong("b_s1");
				this.Si[2] = var3.getLong("b_s2");
				this.Si[3] = var3.getLong("b_s3");
				this.Sl = var3.getInt("c_idx");

				for (int var4 = 0; var4 < this.Sk.length; var4++) {
					long[] var5 = new long[] { var3.getLong("c_s" + var4), var3.getLong("c_e" + var4) };
					this.Sk[var4] = var5;
				}
			} else {
				_log.info("Oly: Generating a new season " + this.RX);
				this.cT();
				this.save();
			}
		} catch (Exception var9) {
			var9.printStackTrace();
		} finally {
			DbUtils.closeQuietly(var1, var2, var3);
		}

		this.Sm = ServerVariables.getInt("@OlyPartCnt", 0);
	}

	public synchronized void save() {
		Connection var1 = null;
		PreparedStatement var2 = null;

		try {
			var1 = DatabaseFactory.getInstance().getConnection();
			var2 = var1.prepareStatement(
					"REPLACE INTO `oly_season`(`season_id`,`season_start_time`,`season_end_time`,`nominate_start`,`b_idx`,`b_s0`,`b_s1`,`b_s2`,`b_s3`,`c_idx`,`c_s0`,`c_e0`,`c_s1`,`c_e1`,`c_s2`,`c_e2`,`c_s3`,`c_e3`,`c_s4`,`c_e4`,`c_s5`,`c_e5`,`c_s6`,`c_e6`,`c_s7`,`c_e7`,`c_s8`,`c_e8`,`c_s9`,`c_e9`,`c_s10`,`c_e10`,`c_s11`,`c_e11`,`c_s12`,`c_e12`,`c_s13`,`c_e13`,`c_s14`,`c_e14`,`c_s15`,`c_e15`,`c_s16`,`c_e16`,`c_s17`,`c_e17`,`c_s18`,`c_e18`,`c_s19`,`c_e19`,`c_s20`,`c_e20`,`c_s21`,`c_e21`,`c_s22`,`c_e22`,`c_s23`,`c_e23`,`c_s24`,`c_e24`,`c_s25`,`c_e25`,`c_s26`,`c_e26`,`c_s27`,`c_e27`,`c_s28`,`c_e28`,`c_s29`,`c_e29`,`c_s30`,`c_e30`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			var2.setInt(1, this.RX);
			var2.setLong(2, this.Sf);
			var2.setLong(3, this.Sg);
			var2.setLong(4, this.Sh);
			var2.setInt(5, this.Sj);

			for (int var3 = 0; var3 < this.Si.length; var3++) {
				var2.setLong(6 + var3, this.Si[var3]);
			}

			var2.setInt(10, this.Sl);

			for (int var9 = 0; var9 < this.Sk.length; var9++) {
				var2.setLong(11 + var9 * 2, this.Sk[var9][0]);
				var2.setLong(12 + var9 * 2, this.Sk[var9][1]);
			}

			var2.executeUpdate();
		} catch (Exception var7) {
			var7.printStackTrace();
		} finally {
			DbUtils.closeQuietly(var1, var2);
		}

		ServerVariables.set("oly_season_id", this.RX);
		ServerVariables.set("oly_season_calc", this.RY);
		ServerVariables.set("@OlyPartCnt", this.Sm);
	}

	private void schedule() {
		long var1 = System.currentTimeMillis() / 1000L;
		int var3 = this.RX;
		long var4 = Math.max(0L, this.Sf - var1);
		if (var4 == 0L) {
			this.Sa = null;
			this.H(var3);
		} else {
			this.Sa = ThreadPoolManager.getInstance().schedule(new OlyController.SeasonStartTask(var3), var4 * 1000L);
			_log.info("OlyController: Season " + var3 + " start schedule at " + c(this.Sa));
		}

		long var6 = Math.max(0L, this.Sg - var1);
		if (var6 == 0L) {
			this.Sb = null;
			this.I(var3);
		} else {
			this.Sb = ThreadPoolManager.getInstance().schedule(new OlyController.SeasonEndTask(var3), var6 * 1000L);
			_log.info("OlyController: Season " + var3 + " end schedule at " + c(this.Sb));
		}

		long var8 = Math.max(0L, this.Sh - var1);
		if (var8 == 0L) {
			this.Sc = null;
			this.J(var3);
		} else {
			this.Sc = ThreadPoolManager.getInstance().schedule(new OlyController.NominationTask(var3), var8 * 1000L);
			_log.info("OlyController: Season " + var3 + " nomination schedule at " + c(this.Sc));
		}

		StringBuilder var10 = new StringBuilder();

		boolean var11 = false;
		long var12 = -1L;

		for (int var14 = this.Sl; var14 < this.Sk.length; var14++) {
			if (this.Sk[var14] != null && this.Sk[var14][0] >= 0L && this.Sk[var14][1] > this.Sk[var14][0]
					&& this.Sk[var14][1] > var1) {
				long var15 = this.Sk[var14][0];
				long var17 = this.Sk[var14][1];
				// On a restart, several old start times can already be in the past.
				// Recover only the competition window that is still active; scheduling all
				// of them with the same fallback delay starts a task storm.
				if (var15 <= var1 && var11 || var12 >= 0L && var15 < var12) {
					continue;
				}

				if (var14 != this.Sl) {
					var10.append(';');
				}

				this.Sd[var14] = new ScheduledFuture[] {
						ThreadPoolManager.getInstance().schedule(new OlyController.CompetitionStartTask(var3, var14),
								Math.max(0L, var15 - var1) * 1000L),
						ThreadPoolManager.getInstance().schedule(new OlyController.CompetitionEndTask(var3, var14),
								Math.max(0L, var17 - var1) * 1000L) };
				var10.append(c(this.Sd[var14][0]) + "-" + c(this.Sd[var14][1]));
				var12 = var17;
				if (var15 <= var1) {
					var11 = true;
				}
			}
		}

		_log.info("OlyController: Season " + var3 + " competitions schedule at [" + var10.toString() + "]");
		var10.delete(0, var10.length());

		for (int var13 = this.Sj; var13 < this.Si.length; var13++) {
			if (this.Si[var13] > var1) {
				if (var13 != this.Sj) {
					var10.append(';');
				}

				this.Se[var13] = ThreadPoolManager.getInstance().schedule(new OlyController.BonusTask(var3, var13),
						Math.max((3 + var13 - this.Sj) * 60, this.Si[var13] - var1) * 1000L);
				var10.append(c(this.Se[var13]));
			}
		}

		_log.info("OlyController: Season " + var3 + " bonuses schedule at [" + var10.toString() + "]");
	}

	private synchronized void H(int var1) {
		try {
			this.RY = false;
			Announcements.getInstance().announceToAll(new SystemMessage(1639).addNumber(var1));
			_log.info("OlyController: Season " + var1 + " started.");
		} catch (Exception var3) {
			_log.warn("Exception while starting of " + var1 + " season", var3);
		}
	}

	private synchronized void I(int var1) {
		try {
			if (!this.RY) {
				this.RY = true;
				if (ServerVariables.getInt("oly_chero_season", -1) != var1) {
					_log.info("OlyController: calculation heroes for " + var1 + " season");
					HeroController.getInstance().ComputeNewHeroNobleses();
					ServerVariables.set("oly_chero_season", var1);
				}

				this.save();
				Announcements.getInstance().announceToAll(new SystemMessage(1640).addNumber(var1));
			} else {
				_log.warn("OlyController: Unexpected season calculated. Canceling computation.");
			}

			_log.info("OlyController: Season " + var1 + " ended.");
		} catch (Exception var3) {
			_log.warn("Exception while ending of " + var1 + " season", var3);
		}
	}

	private synchronized void J(int var1) {
		try {
			if (this.RY) {
				this.RY = false;
				this.save();
			} else {
				_log.warn("OlyController: Season not calculated. Run calculation manualy.");
			}

			_log.info("OlyController: Season " + var1 + " nomination started.");
			ThreadPoolManager.getInstance().execute(new OlyController.NewSeasonCalcTask(var1 + 1));
		} catch (Exception var3) {
			_log.warn("Exception while nominating in " + var1 + " season", var3);
		}
	}

	private synchronized void f(int var1, int var2) {
		try {
			if (!this.RZ) {
				this.RZ = true;
				StadiumPool.getInstance().AllocateStadiums();
				ParticipantPool.getInstance().AllocatePools();
				CompetitionController.getInstance();
				CompetitionController.getInstance().scheduleStartTask();
				Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_STARTED);
				_log.info("OlyController: Season " + var1 + " comp " + var2 + " started.");
				this.St = var2;
			} else {
				_log.debug("OlyController: Ignoring overlapping competition " + var2 + "; competition " + this.St
						+ " is still active.");
			}
		} catch (Exception var4) {
			_log.warn("Exception while start comp " + var2 + " in " + var1 + " season", var4);
		}
	}

	private synchronized void g(int var1, int var2) {
		try {
			if (this.RZ) {
				CompetitionController.getInstance().cancelStartTask();
				this.RZ = false;
				StadiumPool.getInstance().FreeStadiums();
				ParticipantPool.getInstance().FreePools();
				this.St = -1;
				this.Sl = Math.max(this.Sl, var2 + 1);
				Announcements.getInstance().announceToAll(new SystemMessage(1919));
				Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_ENDED);
				_log.info("OlyController: Season " + var1 + " comp " + var2 + " ended.");
				this.save();
			} else {
				_log.warn("OlyController: Can't stop competitions. Competitions not in progress.");
			}
		} catch (Exception var4) {
			_log.warn("Exception while end comp " + var2 + " in " + var1 + " season", var4);
		}
	}

	private synchronized void h(int var1, int var2) {
		try {
			NoblesController.getInstance().AddWeaklyBonus();
			this.Sj++;
			_log.info("OlyController: Season " + var1 + " bonus " + var2 + " applied.");
			this.save();
		} catch (Exception var4) {
			_log.warn("Exception while bonus " + var2 + " in " + var1 + " season", var4);
		}
	}

	private synchronized void K(int var1) {
		try {
			this.save();
			this.RX = var1;
			if (Config.OLY_RECALC_NEW_SEASON) {
				this.cT();
				this.save();
			} else {
				ServerVariables.set("oly_season_id", this.RX);
				this.load();
			}

			this.schedule();
		} catch (Exception var3) {
			_log.warn("Exception while calculating new " + var1 + " season", var3);
		}
	}

	public boolean isCompetitionsActive() {
		return this.RZ;
	}

	public boolean isRegAllowed() {
		if (Config.ALT_OLY_COMPETITION_DAYS.isEmpty()
				|| !Config.ALT_OLY_COMPETITION_DAYS.contains(Calendar.getInstance().get(7))) {
			return false;
		} else {
			return this.RZ && this.St >= 0 ? System.currentTimeMillis() < (this.Sk[this.St][1] - 300L) * 1000L
					: this.RZ;
		}
	}

	public boolean isCalculationPeriod() {
		return this.RY;
	}

	public int getCurrentSeason() {
		return this.RX;
	}

	public int getCurrentPeriod() {
		return this.Sj;
	}

	public void shutdown() {
		if (this.RZ) {
			CompetitionController.getInstance().cancelStartTask();
			StadiumPool.getInstance().FreeStadiums();
			ParticipantPool.getInstance().FreePools();
		}
	}

	public void announceCompetition(CompetitionType var1, int var2) {
		for (NpcInstance var4 : GameObjectsStorage.getAllByNpcId(31688, false)) {
			if (Config.NPC_OLYMPIAD_GAME_ANNOUNCE) {
				switch (var1) {
				case CLASS_FREE:
					Functions.npcShoutCustomMessage(var4, "l2p.gameserver.model.entity.OlympiadGame.OlympiadNonClassed",
							String.valueOf(var2 + 1));
					break;
				case CLASS_INDIVIDUAL:
				case CLASS_TYPE_INDIVIDUAL:
					Functions.npcShoutCustomMessage(var4, "l2p.gameserver.model.entity.OlympiadGame.OlympiadClassed",
							String.valueOf(var2 + 1));
					break;
				case TEAM_CLASS_FREE:
					Functions.npcShoutCustomMessage(var4, "l2p.gameserver.model.entity.OlympiadGame.OlympiadTeam",
							String.valueOf(var2 + 1));
				}
			}
		}
	}

	public int getPartCount() {
		return this.Sm;
	}

	public int getCurrentPartCount() {
		return CompetitionController.getInstance().getCompetitions().size();
	}

	public void incPartCount() {
		this.Sm++;
	}

	private final synchronized void cT() {
		Calendar var1 = Calendar.getInstance();
		if (Config.OLY_SEASON_TIME_CALC_MODE == Config.OlySeasonTimeCalcMode.NORMAL) {
			var1.set(5, 1);
		} else {
			var1.set(5, var1.get(5));
		}

		var1.set(11, 0);
		var1.set(12, 0);
		var1.set(13, 0);
		var1.set(14, 0);
		long var2 = var1.getTimeInMillis();
		this.Sf = this.a(var2, Config.OLY_SEASON_START_TIME);
		this.Sg = this.a(var2, Config.OLY_SEASON_END_TIME);
		this.Sh = this.a(var2, Config.OLY_NOMINATE_TIME);
		var2 = this.Sf * 1000L;
		Calendar var4 = Calendar.getInstance();
		var4.setTimeInMillis(var2);

		for (int var5 = 0; var5 < this.Si.length; var5++) {
			this.Si[var5] = this.a(var4, Config.OLY_BONUS_TIME);
		}

		this.Sj = 0;
		Calendar var8 = Calendar.getInstance();
		var8.setTimeInMillis(var2);

		for (int var6 = 0; var6 < this.Sk.length; var6++) {
			this.Sk[var6] = new long[2];
			this.Sk[var6][0] = this.a(var8, Config.OLY_COMPETITION_START_TIME);
			this.Sk[var6][1] = this.a(var8, Config.OLY_COMPETITION_END_TIME);
			if (this.Sk[var6][0] > this.Sg) {
				this.Sk[var6][0] = -1L;
				this.Sk[var6][1] = -1L;
			}

			if (this.Sk[var6][1] >= this.Sg - 300L) {
				this.Sk[var6][1] = this.Sk[var6][1] - 300L;
			}
		}

		this.Sl = 0;
	}

	private long a(long var1, String var3) {
		Calendar var4 = Calendar.getInstance();
		var4.setTime(new Date(var1));
		return this.a(var4, var3);
	}

	private long a(Calendar var1, String var2) {
		String[] var3 = var2.split("\\s+");
		if (var3.length == 2) {
			String var4 = var3[0];
			String[] var5 = var4.split(":");
			if (var5.length == 2) {
				if (var5[0].startsWith("+")) {
					var1.add(2, Integer.parseInt(var5[0].substring(1)));
				} else {
					var1.set(2, Integer.parseInt(var5[0]) - 1);
				}
			}

			String var6 = var5[var5.length - 1];
			if (var6.startsWith("+")) {
				var1.add(5, Integer.parseInt(var6.substring(1)));
			} else {
				var1.set(5, Integer.parseInt(var6));
			}
		}

		String[] var7 = var3[var3.length - 1].split(":");
		if (var7[0].startsWith("+")) {
			var1.add(11, Integer.parseInt(var7[0].substring(1)));
		} else {
			var1.set(11, Integer.parseInt(var7[0]));
		}

		if (var7[1].startsWith("+")) {
			var1.add(12, Integer.parseInt(var7[1].substring(1)));
		} else {
			var1.set(12, Integer.parseInt(var7[1]));
		}

		return var1.getTimeInMillis() / 1000L;
	}

	public long getSeasonStartTime() {
		return this.Sf;
	}

	public long getSeasonEndTime() {
		return this.Sg;
	}

	private static String c(ScheduledFuture<?> var0) {
		return m(System.currentTimeMillis() / 1000L + var0.getDelay(TimeUnit.SECONDS));
	}

	private static String m(long var0) {
		return RV.format(new Date((var0 + 1L) * 1000L));
	}

	static {
		RV.setTimeZone(TimeZone.getDefault());
	}

	private class BonusTask implements Runnable {
		private int Su;
		private int Sv;

		public BonusTask(int var2, int var3) {
			this.Su = var2;
			this.Sv = var3;
		}

		@Override
		public void run() {
			OlyController.getInstance().h(this.Su, this.Sv);
		}
	}

	private class CompetitionEndTask implements Runnable {
		private int Su;
		private int Sw;

		public CompetitionEndTask(int var2, int var3) {
			this.Su = var2;
			this.Sw = var3;
		}

		@Override
		public void run() {
			OlyController.getInstance().g(this.Su, this.Sw);
		}
	}

	private class CompetitionStartTask implements Runnable {
		private int Su;
		private int Sw;

		public CompetitionStartTask(int var2, int var3) {
			this.Su = var2;
			this.Sw = var3;
		}

		@Override
		public void run() {
			OlyController.getInstance().f(this.Su, this.Sw);
		}
	}

	private class NewSeasonCalcTask implements Runnable {
		private int Su;

		public NewSeasonCalcTask(int var2) {
			this.Su = var2;
		}

		@Override
		public void run() {
			OlyController.getInstance().K(this.Su);
		}
	}

	private class NominationTask implements Runnable {
		private int Su;

		public NominationTask(int var2) {
			this.Su = var2;
		}

		@Override
		public void run() {
			OlyController.getInstance().J(this.Su);
		}
	}

	private class SeasonEndTask implements Runnable {
		private int Su;

		public SeasonEndTask(int var2) {
			this.Su = var2;
		}

		@Override
		public void run() {
			OlyController.getInstance().I(this.Su);
		}
	}

	private class SeasonStartTask implements Runnable {
		private int Su;

		public SeasonStartTask(int var2) {
			this.Su = var2;
		}

		@Override
		public void run() {
			OlyController.getInstance().H(this.Su);
		}
	}
}
