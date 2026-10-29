package l2.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import l2.commons.dbutils.DbUtils;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.listener.GameListener;
import l2.gameserver.listener.game.OnSSPeriodListener;
import l2.gameserver.listener.game.OnStartListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.s2c.SSQInfo;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SevenSigns {
   private static final Logger _log = LoggerFactory.getLogger(SevenSigns.class);
   private static SevenSigns PN;
   private ScheduledFuture<?> PO;
   public static final String SEVEN_SIGNS_HTML_PATH = "seven_signs/";
   public static final int CABAL_NULL = 0;
   public static final int CABAL_DUSK = 1;
   public static final int CABAL_DAWN = 2;
   public static final int SEAL_NULL = 0;
   public static final int SEAL_AVARICE = 1;
   public static final int SEAL_GNOSIS = 2;
   public static final int SEAL_STRIFE = 3;
   public static final int PERIOD_COMP_RECRUITING = 0;
   public static final int PERIOD_COMPETITION = 1;
   public static final int PERIOD_COMP_RESULTS = 2;
   public static final int PERIOD_SEAL_VALIDATION = 3;
   public static final int PERIOD_START_HOUR = 18;
   public static final int PERIOD_START_MINS = 0;
   public static final int PERIOD_START_DAY = 2;
   public static final int PERIOD_MINOR_LENGTH = 900000;
   public static final int PERIOD_MAJOR_LENGTH = 603900000;
   public static final int ANCIENT_ADENA_ID = 5575;
   public static final int RECORD_SEVEN_SIGNS_ID = 5707;
   public static final int CERTIFICATE_OF_APPROVAL_ID = 6388;
   public static final int RECORD_SEVEN_SIGNS_COST = 500;
   public static final int ADENA_JOIN_DAWN_COST = 50000;
   public static final Set<Integer> ORATOR_NPC_IDS = new HashSet<>(
      Arrays.asList(
         31093,
         31172,
         31174,
         31176,
         31178,
         31180,
         31182,
         31184,
         31186,
         31188,
         31190,
         31192,
         31194,
         31196,
         31198,
         31200,
         31231,
         31232,
         31233,
         31234,
         31235,
         31236,
         31237,
         31238,
         31239,
         31240,
         31241,
         31242,
         31243,
         31244,
         31245,
         31246,
         31713,
         31714,
         31715,
         31716,
         31717,
         31718,
         31719,
         31720
      )
   );
   public static final Set<Integer> PREACHER_NPC_IDS = new HashSet<>(
      Arrays.asList(
         31094,
         31173,
         31175,
         31177,
         31179,
         31181,
         31183,
         31185,
         31187,
         31189,
         31191,
         31193,
         31195,
         31197,
         31199,
         31201,
         31247,
         31248,
         31249,
         31250,
         31251,
         31252,
         31253,
         31254,
         31721,
         31722,
         31723,
         31724,
         31725,
         31726,
         31727,
         31728,
         32003,
         32004,
         32005,
         32006
      )
   );
   public static final int SEAL_STONE_BLUE_ID = 6360;
   public static final int SEAL_STONE_GREEN_ID = 6361;
   public static final int SEAL_STONE_RED_ID = 6362;
   public static final int SEAL_STONE_BLUE_VALUE = 3;
   public static final int SEAL_STONE_GREEN_VALUE = 5;
   public static final int SEAL_STONE_RED_VALUE = 10;
   public static final int BLUE_CONTRIB_POINTS = 3;
   public static final int GREEN_CONTRIB_POINTS = 5;
   public static final int RED_CONTRIB_POINTS = 10;
   public static final long MAXIMUM_PLAYER_CONTRIB = Math.round(1000000.0 * Config.RATE_DROP_ITEMS);
   private final Calendar PP = Calendar.getInstance();
   protected int _activePeriod;
   protected int _currentCycle;
   protected long _dawnStoneScore;
   protected long _duskStoneScore;
   protected long _dawnFestivalScore;
   protected long _duskFestivalScore;
   protected int _compWinner;
   protected int _previousWinner;
   private Map<Integer, StatsSet> PQ;
   private Map<Integer, Integer> PR;
   private Map<Integer, Integer> PS;
   private Map<Integer, Integer> PT;
   private SevenSigns.SSListenerList PU = new SevenSigns.SSListenerList();

   public SevenSigns() {
      GameServer.getInstance().addListener(new SevenSigns.OnStartListenerImpl());
      this.PQ = new ConcurrentHashMap<>();
      this.PR = new ConcurrentHashMap<>();
      this.PS = new ConcurrentHashMap<>();
      this.PT = new ConcurrentHashMap<>();

      try {
         this.restoreSevenSignsData();
      } catch (Exception var10) {
         _log.error("SevenSigns: Failed to load configuration: " + var10);
         _log.error("", var10);
      }

      _log.info("SevenSigns: Currently in the " + this.getCurrentPeriodName() + " period!");
      this.initializeSeals();
      if (this.isSealValidationPeriod()) {
         if (this.getCabalHighestScore() == 0) {
            _log.info("SevenSigns: The Competition last week ended with a tie.");
         } else {
            _log.info("SevenSigns: The " + getCabalName(this.getCabalHighestScore()) + " were victorious last week.");
         }
      } else if (this.getCabalHighestScore() == 0) {
         _log.info("SevenSigns: The Competition this week, if the trend continue, will end with a tie.");
      } else {
         _log.info("SevenSigns: The " + getCabalName(this.getCabalHighestScore()) + " are in the lead this week.");
      }

      int var1 = 0;
      int var2 = 0;
      int var3 = 0;
      this.setCalendarForNextPeriodChange();
      long var4 = this.getMilliToPeriodChange();
      if (var4 < 10L) {
         var4 = 10L;
      }

      this.PO = ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsPeriodChange(), var4);
      double var6 = var4 / 1000L % 60L;
      double var8 = (var4 / 1000L - var6) / 60.0;
      var1 = (int)Math.floor(var8 % 60.0);
      var8 = (var8 - var1) / 60.0;
      var2 = (int)Math.floor(var8 % 24.0);
      var3 = (int)Math.floor((var8 - var2) / 24.0);
      _log.info("SevenSigns: Next period begins in " + var3 + " days, " + var2 + " hours and " + var1 + " mins.");
      if (Config.SS_ANNOUNCE_PERIOD > 0) {
         ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsAnnounce(), Config.SS_ANNOUNCE_PERIOD * 1000L * 60L);
      }
   }

   public static SevenSigns getInstance() {
      if (PN == null) {
         PN = new SevenSigns();
      }

      return PN;
   }

   public static long calcContributionScore(long var0, long var2, long var4) {
      long var6 = var0 * 3L;
      var6 += var2 * 5L;
      return var6 + var4 * 10L;
   }

   public static long calcAncientAdenaReward(long var0, long var2, long var4) {
      long var6 = var0 * 3L;
      var6 += var2 * 5L;
      return var6 + var4 * 10L;
   }

   public static int getCabalNumber(String var0) {
      if (var0.equalsIgnoreCase("dawn")) {
         return 2;
      } else {
         return var0.equalsIgnoreCase("dusk") ? 1 : 0;
      }
   }

   public static String getCabalShortName(int var0) {
      switch (var0) {
         case 1:
            return "dusk";
         case 2:
            return "dawn";
         default:
            return "No Cabal";
      }
   }

   public static String getCabalName(int var0) {
      switch (var0) {
         case 1:
            return "Revolutionaries of Dusk";
         case 2:
            return "Lords of Dawn";
         default:
            return "No Cabal";
      }
   }

   public static String getSealName(int var0, boolean var1) {
      String var2 = !var1 ? "Seal of " : "";
      switch (var0) {
         case 1:
            var2 = var2 + "Avarice";
            break;
         case 2:
            var2 = var2 + "Gnosis";
            break;
         case 3:
            var2 = var2 + "Strife";
      }

      return var2;
   }

   public static String capitalizeWords(String var0) {
      char[] var1 = var0.toCharArray();
      StringBuilder var2 = new StringBuilder();
      var1[0] = Character.toUpperCase(var1[0]);

      for (int var3 = 0; var3 < var1.length; var3++) {
         if (Character.isWhitespace(var1[var3]) && var3 != var1.length - 1) {
            var1[var3 + 1] = Character.toUpperCase(var1[var3 + 1]);
         }

         var2.append(Character.toString(var1[var3]));
      }

      return var2.toString();
   }

   public final int getCurrentCycle() {
      return this._currentCycle;
   }

   public final int getCurrentPeriod() {
      return this._activePeriod;
   }

   private int cH() {
      int var1 = this.PP.get(7) - 2;
      return var1 < 0 ? 0 - var1 : 7 - var1;
   }

   public final long getMilliToPeriodChange() {
      return this.PP.getTimeInMillis() - System.currentTimeMillis();
   }

   protected void setCalendarForNextPeriodChange() {
      switch (this.getCurrentPeriod()) {
         case 0:
         case 2:
            this.PP.add(14, 900000);
            break;
         case 1:
         case 3:
            int var1 = this.cH();
            if (var1 == 7) {
               if (this.PP.get(11) < 18) {
                  var1 = 0;
               } else if (this.PP.get(11) == 18 && this.PP.get(12) < 0) {
                  var1 = 0;
               }
            }

            if (var1 > 0) {
               this.PP.add(5, var1);
            }

            this.PP.set(11, 18);
            this.PP.set(12, 0);
      }
   }

   public final String getCurrentPeriodName() {
      String var1 = null;
      switch (this._activePeriod) {
         case 0:
            var1 = "Quest Event Initialization";
            break;
         case 1:
            var1 = "Competition (Quest Event)";
            break;
         case 2:
            var1 = "Quest Event Results";
            break;
         case 3:
            var1 = "Seal Validation";
      }

      return var1;
   }

   public final boolean isSealValidationPeriod() {
      return this._activePeriod == 3;
   }

   public final boolean isCompResultsPeriod() {
      return this._activePeriod == 2;
   }

   public final long getCurrentScore(int var1) {
      double var2 = this._dawnStoneScore + this._duskStoneScore;
      switch (var1) {
         case 0:
            return 0L;
         case 1:
            return Math.round(this._duskStoneScore / (var2 == 0.0 ? 1.0 : var2) * 500.0) + this._duskFestivalScore;
         case 2:
            return Math.round(this._dawnStoneScore / (var2 == 0.0 ? 1.0 : var2) * 500.0) + this._dawnFestivalScore;
         default:
            return 0L;
      }
   }

   public final long getCurrentStoneScore(int var1) {
      switch (var1) {
         case 0:
            return 0L;
         case 1:
            return this._duskStoneScore;
         case 2:
            return this._dawnStoneScore;
         default:
            return 0L;
      }
   }

   public final long getCurrentFestivalScore(int var1) {
      switch (var1) {
         case 0:
            return 0L;
         case 1:
            return this._duskFestivalScore;
         case 2:
            return this._dawnFestivalScore;
         default:
            return 0L;
      }
   }

   public final int getCabalHighestScore() {
      long var1 = this.getCurrentScore(1) - this.getCurrentScore(2);
      if (var1 == 0L) {
         return 0;
      } else {
         return var1 > 0L ? 1 : 2;
      }
   }

   public final int getSealOwner(int var1) {
      return this.PR != null && this.PR.containsKey(var1) ? this.PR.get(var1) : 0;
   }

   public final int getSealProportion(int var1, int var2) {
      if (var2 == 0) {
         return 0;
      } else {
         return var2 == 1 ? this.PS.get(var1) : this.PT.get(var1);
      }
   }

   public final int getTotalMembers(int var1) {
      int var2 = 0;

      for (StatsSet var4 : this.PQ.values()) {
         if (var4.getInteger("cabal") == var1) {
            var2++;
         }
      }

      return var2;
   }

   public final StatsSet getPlayerStatsSet(Player var1) {
      return !this.F(var1.getObjectId()) ? null : this.PQ.get(var1.getObjectId());
   }

   public long getPlayerStoneContrib(Player var1) {
      if (!this.F(var1.getObjectId())) {
         return 0L;
      }

      long var2 = 0L;
      StatsSet var4 = this.PQ.get(var1.getObjectId());
      if (this.getPlayerCabal(var1) == 2) {
         var2 += var4.getLong("dawn_red_stones");
         var2 += var4.getLong("dawn_green_stones");
         var2 += var4.getLong("dawn_blue_stones");
      } else {
         var2 += var4.getLong("dusk_red_stones");
         var2 += var4.getLong("dusk_green_stones");
         var2 += var4.getLong("dusk_blue_stones");
      }

      return var2;
   }

   public long getPlayerContribScore(Player var1) {
      if (!this.F(var1.getObjectId())) {
         return 0L;
      }

      StatsSet var2 = this.PQ.get(var1.getObjectId());
      return this.getPlayerCabal(var1) == 2 ? var2.getInteger("dawn_contribution_score") : var2.getInteger("dusk_contribution_score");
   }

   public long getPlayerAdenaCollect(Player var1) {
      return !this.F(var1.getObjectId())
         ? 0L
         : this.PQ.get(var1.getObjectId()).getLong(this.getPlayerCabal(var1) == 2 ? "dawn_ancient_adena_amount" : "dusk_ancient_adena_amount");
   }

   public int getPlayerSeal(Player var1) {
      return !this.F(var1.getObjectId()) ? 0 : this.PQ.get(var1.getObjectId()).getInteger("seal");
   }

   public int getPlayerCabal(Player var1) {
      return !this.F(var1.getObjectId()) ? 0 : this.PQ.get(var1.getObjectId()).getInteger("cabal");
   }

   protected void restoreSevenSignsData() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement(
            "SELECT char_obj_id, cabal, seal, dawn_red_stones, dawn_green_stones, dawn_blue_stones, dawn_ancient_adena_amount, dawn_contribution_score, dusk_red_stones, dusk_green_stones, dusk_blue_stones, dusk_ancient_adena_amount, dusk_contribution_score FROM seven_signs"
         );
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("char_obj_id");
            StatsSet var5 = new StatsSet();
            var5.set("char_obj_id", var4);
            var5.set("cabal", getCabalNumber(var3.getString("cabal")));
            var5.set("seal", var3.getInt("seal"));
            var5.set("dawn_red_stones", var3.getInt("dawn_red_stones"));
            var5.set("dawn_green_stones", var3.getInt("dawn_green_stones"));
            var5.set("dawn_blue_stones", var3.getInt("dawn_blue_stones"));
            var5.set("dawn_ancient_adena_amount", var3.getInt("dawn_ancient_adena_amount"));
            var5.set("dawn_contribution_score", var3.getInt("dawn_contribution_score"));
            var5.set("dusk_red_stones", var3.getInt("dusk_red_stones"));
            var5.set("dusk_green_stones", var3.getInt("dusk_green_stones"));
            var5.set("dusk_blue_stones", var3.getInt("dusk_blue_stones"));
            var5.set("dusk_ancient_adena_amount", var3.getInt("dusk_ancient_adena_amount"));
            var5.set("dusk_contribution_score", var3.getInt("dusk_contribution_score"));
            this.PQ.put(var4, var5);
         }

         DbUtils.close(var2, var3);
         var2 = var1.prepareStatement("SELECT * FROM `seven_signs_status`");
         var3 = var2.executeQuery();

         while (var3.next()) {
            this._currentCycle = var3.getInt("current_cycle");
            this._activePeriod = var3.getInt("active_period");
            this._previousWinner = var3.getInt("previous_winner");
            this._dawnStoneScore = var3.getLong("dawn_stone_score");
            this._dawnFestivalScore = var3.getLong("dawn_festival_score");
            this._duskStoneScore = var3.getLong("dusk_stone_score");
            this._duskFestivalScore = var3.getLong("dusk_festival_score");
            this.PR.put(1, var3.getInt("avarice_owner"));
            this.PR.put(2, var3.getInt("gnosis_owner"));
            this.PR.put(3, var3.getInt("strife_owner"));
            this.PT.put(1, var3.getInt("avarice_dawn_score"));
            this.PT.put(2, var3.getInt("gnosis_dawn_score"));
            this.PT.put(3, var3.getInt("strife_dawn_score"));
            this.PS.put(1, var3.getInt("avarice_dusk_score"));
            this.PS.put(2, var3.getInt("gnosis_dusk_score"));
            this.PS.put(3, var3.getInt("strife_dusk_score"));
         }

         DbUtils.close(var2, var3);
         var2 = var1.prepareStatement("UPDATE `seven_signs_status` SET `date`=?");
         var2.setInt(1, Calendar.getInstance().get(7));
         var2.execute();
      } catch (SQLException var9) {
         _log.error("Unable to load Seven Signs Data: " + var9);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public synchronized void saveSevenSignsData(int var1, boolean var2) {
      Connection var3 = null;
      PreparedStatement var4 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement(
            "UPDATE `seven_signs` SET `cabal`=?, `seal`=?, `dawn_red_stones`=?, `dawn_green_stones`=?, `dawn_blue_stones`=?, `dawn_ancient_adena_amount`=?, `dawn_contribution_score`=?, `dusk_red_stones`=?, `dusk_green_stones`=?, `dusk_blue_stones`=?, `dusk_ancient_adena_amount`=?, `dusk_contribution_score`=? WHERE `char_obj_id`=?"
         );
         if (var1 > 0) {
            a(var4, this.PQ.get(var1));
         } else {
            for (StatsSet var6 : this.PQ.values()) {
               a(var4, var6);
            }
         }

         DbUtils.close(var4);
         if (var2) {
            StringBuilder var12 = new StringBuilder();
            var12.append(
               "UPDATE `seven_signs_status` SET `current_cycle`=?, `active_period`=?, `previous_winner`=?, `dawn_stone_score`=?, `dawn_festival_score`=?, `dusk_stone_score`=?, `dusk_festival_score`=?, `avarice_owner`=?, `gnosis_owner`=?, `strife_owner`=?, `avarice_dawn_score`=?, `gnosis_dawn_score`=?, `strife_dawn_score`=?, `avarice_dusk_score`=?, `gnosis_dusk_score`=?, `strife_dusk_score`=?, `festival_cycle`=?, "
            );

            for (int var13 = 0; var13 < 5; var13++) {
               var12.append("accumulated_bonus" + String.valueOf(var13) + "=?, ");
            }

            var12.append("date=?");
            var4 = var3.prepareStatement(var12.toString());
            var4.setInt(1, this._currentCycle);
            var4.setInt(2, this._activePeriod);
            var4.setInt(3, this._previousWinner);
            var4.setLong(4, this._dawnStoneScore);
            var4.setLong(5, this._dawnFestivalScore);
            var4.setLong(6, this._duskStoneScore);
            var4.setLong(7, this._duskFestivalScore);
            var4.setInt(8, this.PR.get(1));
            var4.setInt(9, this.PR.get(2));
            var4.setInt(10, this.PR.get(3));
            var4.setInt(11, this.PT.get(1));
            var4.setInt(12, this.PT.get(2));
            var4.setInt(13, this.PT.get(3));
            var4.setInt(14, this.PS.get(1));
            var4.setInt(15, this.PS.get(2));
            var4.setInt(16, this.PS.get(3));
            var4.setInt(17, this.getCurrentCycle());

            for (int var14 = 0; var14 < 5; var14++) {
               var4.setLong(18 + var14, SevenSignsFestival.getInstance().getAccumulatedBonus(var14));
            }

            var4.setInt(23, Calendar.getInstance().get(7));
            var4.executeUpdate();
         }
      } catch (SQLException var10) {
         _log.error("Unable to save Seven Signs data: " + var10);
         _log.error("", var10);
      } finally {
         DbUtils.closeQuietly(var3, var4);
      }
   }

   private static void a(PreparedStatement var0, StatsSet var1) throws SQLException {
      var0.setString(1, getCabalShortName(var1.getInteger("cabal")));
      var0.setInt(2, var1.getInteger("seal"));
      var0.setInt(3, var1.getInteger("dawn_red_stones"));
      var0.setInt(4, var1.getInteger("dawn_green_stones"));
      var0.setInt(5, var1.getInteger("dawn_blue_stones"));
      var0.setInt(6, var1.getInteger("dawn_ancient_adena_amount"));
      var0.setInt(7, var1.getInteger("dawn_contribution_score"));
      var0.setInt(8, var1.getInteger("dusk_red_stones"));
      var0.setInt(9, var1.getInteger("dusk_green_stones"));
      var0.setInt(10, var1.getInteger("dusk_blue_stones"));
      var0.setInt(11, var1.getInteger("dusk_ancient_adena_amount"));
      var0.setInt(12, var1.getInteger("dusk_contribution_score"));
      var0.setInt(13, var1.getInteger("char_obj_id"));
      var0.executeUpdate();
   }

   protected void resetPlayerData() {
      for (StatsSet var2 : this.PQ.values()) {
         int var3 = var2.getInteger("char_obj_id");
         if (var2.getInteger("cabal") == this.getCabalHighestScore()) {
            switch (this.getCabalHighestScore()) {
               case 1:
                  var2.set("dusk_red_stones", 0);
                  var2.set("dusk_green_stones", 0);
                  var2.set("dusk_blue_stones", 0);
                  var2.set("dusk_contribution_score", 0);
                  break;
               case 2:
                  var2.set("dawn_red_stones", 0);
                  var2.set("dawn_green_stones", 0);
                  var2.set("dawn_blue_stones", 0);
                  var2.set("dawn_contribution_score", 0);
            }
         } else if (var2.getInteger("cabal") != 2 && var2.getInteger("cabal") != 0) {
            if (var2.getInteger("cabal") == 1 || var2.getInteger("cabal") == 0) {
               var2.set("dawn_red_stones", 0);
               var2.set("dawn_green_stones", 0);
               var2.set("dawn_blue_stones", 0);
               var2.set("dawn_contribution_score", 0);
            }
         } else {
            var2.set("dusk_red_stones", 0);
            var2.set("dusk_green_stones", 0);
            var2.set("dusk_blue_stones", 0);
            var2.set("dusk_contribution_score", 0);
         }

         var2.set("cabal", 0);
         var2.set("seal", 0);
         this.PQ.put(var3, var2);
      }
   }

   private boolean F(int var1) {
      return this.PQ.containsKey(var1);
   }

   public int setPlayerInfo(int var1, int var2, int var3) {
      StatsSet var4 = null;
      if (this.F(var1)) {
         var4 = this.PQ.get(var1);
         var4.set("cabal", var2);
         var4.set("seal", var3);
         this.PQ.put(var1, var4);
      } else {
         var4 = new StatsSet();
         var4.set("char_obj_id", var1);
         var4.set("cabal", var2);
         var4.set("seal", var3);
         var4.set("dawn_red_stones", 0);
         var4.set("dawn_green_stones", 0);
         var4.set("dawn_blue_stones", 0);
         var4.set("dawn_ancient_adena_amount", 0);
         var4.set("dawn_contribution_score", 0);
         var4.set("dusk_red_stones", 0);
         var4.set("dusk_green_stones", 0);
         var4.set("dusk_blue_stones", 0);
         var4.set("dusk_ancient_adena_amount", 0);
         var4.set("dusk_contribution_score", 0);
         this.PQ.put(var1, var4);
         Connection var5 = null;
         PreparedStatement var6 = null;

         try {
            var5 = DatabaseFactory.getInstance().getConnection();
            var6 = var5.prepareStatement("INSERT INTO `seven_signs` (`char_obj_id`, `cabal`, `seal`) VALUES (?,?,?)");
            var6.setInt(1, var1);
            var6.setString(2, getCabalShortName(var2));
            var6.setInt(3, var3);
            var6.execute();
         } catch (SQLException var11) {
            _log.error("SevenSigns: Failed to save data: " + var11);
         } finally {
            DbUtils.closeQuietly(var5, var6);
         }
      }

      long var14 = 0L;
      switch (var2) {
         case 1:
            var14 = calcContributionScore(var4.getInteger("dusk_blue_stones"), var4.getInteger("dusk_green_stones"), var4.getInteger("dusk_red_stones"));
            this._duskStoneScore += var14;
            break;
         case 2:
            var14 = calcContributionScore(var4.getInteger("dawn_blue_stones"), var4.getInteger("dawn_green_stones"), var4.getInteger("dawn_red_stones"));
            this._dawnStoneScore += var14;
      }

      if (var4.getInteger("cabal") == 2) {
         this.PT.put(var3, this.PT.get(var3) + 1);
      } else {
         this.PS.put(var3, this.PS.get(var3) + 1);
      }

      this.saveSevenSignsData(var1, true);
      return var2;
   }

   public int getAncientAdenaReward(Player var1, boolean var2) {
      int var3 = var1.getObjectId();
      StatsSet var4 = this.PQ.get(var3);
      int var5 = 0;
      if (var4.getInteger("cabal") == 2) {
         var5 = var4.getInteger("dawn_ancient_adena_amount");
         var4.set("dawn_ancient_adena_amount", 0);
      } else {
         var5 = var4.getInteger("dusk_ancient_adena_amount");
         var4.set("dusk_ancient_adena_amount", 0);
      }

      if (var2) {
         this.PQ.put(var3, var4);
         this.saveSevenSignsData(var3, false);
      }

      return var5;
   }

   public long addPlayerStoneContrib(Player var1, long var2, long var4, long var6) {
      return this.addPlayerStoneContrib(var1.getObjectId(), var2, var4, var6);
   }

   public long addPlayerStoneContrib(int var1, long var2, long var4, long var6) {
      StatsSet var8 = this.PQ.get(var1);
      long var9 = calcContributionScore(var2, var4, var6);
      long var11 = 0L;
      long var13 = 0L;
      if (var8.getInteger("cabal") == 2) {
         var11 = var8.getInteger("dawn_ancient_adena_amount") + calcAncientAdenaReward(var2, var4, var6);
         var13 = var8.getInteger("dawn_contribution_score") + var9;
         if (var13 > MAXIMUM_PLAYER_CONTRIB) {
            return -1L;
         }

         var8.set("dawn_red_stones", var8.getInteger("dawn_red_stones") + var6);
         var8.set("dawn_green_stones", var8.getInteger("dawn_green_stones") + var4);
         var8.set("dawn_blue_stones", var8.getInteger("dawn_blue_stones") + var2);
         var8.set("dawn_ancient_adena_amount", var11);
         var8.set("dawn_contribution_score", var13);
         this.PQ.put(var1, var8);
         this._dawnStoneScore += var9;
      } else {
         var11 = var8.getInteger("dusk_ancient_adena_amount") + calcAncientAdenaReward(var2, var4, var6);
         var13 = var8.getInteger("dusk_contribution_score") + var9;
         if (var13 > MAXIMUM_PLAYER_CONTRIB) {
            return -1L;
         }

         var8.set("dusk_red_stones", var8.getInteger("dusk_red_stones") + var6);
         var8.set("dusk_green_stones", var8.getInteger("dusk_green_stones") + var4);
         var8.set("dusk_blue_stones", var8.getInteger("dusk_blue_stones") + var2);
         var8.set("dusk_ancient_adena_amount", var11);
         var8.set("dusk_contribution_score", var13);
         this.PQ.put(var1, var8);
         this._duskStoneScore += var9;
      }

      this.saveSevenSignsData(var1, true);
      return var9;
   }

   public synchronized void updateFestivalScore() {
      this._duskFestivalScore = 0L;
      this._dawnFestivalScore = 0L;

      for (int var1 = 0; var1 < 5; var1++) {
         long var2 = SevenSignsFestival.getInstance().getHighestScore(1, var1);
         long var4 = SevenSignsFestival.getInstance().getHighestScore(2, var1);
         if (var2 > var4) {
            this._duskFestivalScore = this._duskFestivalScore + SevenSignsFestival.FESTIVAL_LEVEL_SCORES[var1];
         } else if (var2 < var4) {
            this._dawnFestivalScore = this._dawnFestivalScore + SevenSignsFestival.FESTIVAL_LEVEL_SCORES[var1];
         }
      }
   }

   public void sendCurrentPeriodMsg(Player var1) {
      switch (this._activePeriod) {
         case 0:
            var1.sendPacket(Msg.SEVEN_SIGNS_PREPARATIONS_HAVE_BEGUN_FOR_THE_NEXT_QUEST_EVENT);
            return;
         case 1:
            var1.sendPacket(
               Msg.SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_BEGUN_SPEAK_WITH_A_PRIEST_OF_DAWN_OR_DUSK_PRIESTESS_IF_YOU_WISH_TO_PARTICIPATE_IN_THE_EVENT
            );
            return;
         case 2:
            var1.sendPacket(Msg.SEVEN_SIGNS_QUEST_EVENT_HAS_ENDED_RESULTS_ARE_BEING_TALLIED);
            return;
         case 3:
            var1.sendPacket(Msg.SEVEN_SIGNS_THIS_IS_THE_SEAL_VALIDATION_PERIOD_A_NEW_QUEST_EVENT_PERIOD_BEGINS_NEXT_MONDAY);
            return;
      }
   }

   public void sendMessageToAll(int var1) {
      SystemMessage var2 = new SystemMessage(var1);

      for (Player var4 : GameObjectsStorage.getAllPlayersForIterate()) {
         var4.sendPacket(var2);
      }
   }

   protected void initializeSeals() {
      for (Integer var2 : this.PR.keySet()) {
         int var3 = this.PR.get(var2);
         if (var3 != 0) {
            if (this.isSealValidationPeriod()) {
               _log.info("SevenSigns: The " + getCabalName(var3) + " have won the " + getSealName(var2, false) + ".");
            } else {
               _log.info("SevenSigns: The " + getSealName(var2, false) + " is currently owned by " + getCabalName(var3) + ".");
            }
         } else {
            _log.info("SevenSigns: The " + getSealName(var2, false) + " remains unclaimed.");
         }
      }
   }

   protected void resetSeals() {
      this.PT.put(1, 0);
      this.PT.put(2, 0);
      this.PT.put(3, 0);
      this.PS.put(1, 0);
      this.PS.put(2, 0);
      this.PS.put(3, 0);
   }

   protected void calcNewSealOwners() {
      for (Integer var2 : this.PT.keySet()) {
         int var4;
         int var3 = this.PR.get(var2);
         var4 = 0;
         int var5 = this.getSealProportion(var2, 2);
         int var6 = this.getTotalMembers(2) == 0 ? 1 : this.getTotalMembers(2);
         int var7 = this.getSealProportion(var2, 1);
         int var8 = this.getTotalMembers(1) == 0 ? 1 : this.getTotalMembers(1);
         label126:
         switch (var3) {
            case 0:
               switch (this.getCabalHighestScore()) {
                  case 0:
                     if (var5 >= Math.round(0.35 * var6) && var5 > var7) {
                        var4 = 2;
                     } else if (var7 >= Math.round(0.35 * var8) && var7 > var5) {
                        var4 = 1;
                     } else {
                        var4 = var3;
                     }
                     break label126;
                  case 1:
                     if (var7 >= Math.round(0.35 * var8)) {
                        var4 = 1;
                     } else if (var5 >= Math.round(0.35 * var6)) {
                        var4 = 2;
                     } else {
                        var4 = var3;
                     }
                     break label126;
                  case 2:
                     if (var5 >= Math.round(0.35 * var6)) {
                        var4 = 2;
                     } else if (var7 >= Math.round(0.35 * var8)) {
                        var4 = 1;
                     } else {
                        var4 = var3;
                     }
                  default:
                     break label126;
               }
            case 1:
               switch (this.getCabalHighestScore()) {
                  case 0:
                     if (var7 >= Math.round(0.1 * var8)) {
                        var4 = var3;
                     } else if (var5 >= Math.round(0.35 * var6)) {
                        var4 = 2;
                     } else {
                        var4 = 0;
                     }
                     break label126;
                  case 1:
                     if (var7 >= Math.round(0.1 * var8)) {
                        var4 = var3;
                     } else if (var5 >= Math.round(0.35 * var6)) {
                        var4 = 2;
                     } else {
                        var4 = 0;
                     }
                     break label126;
                  case 2:
                     if (var5 >= Math.round(0.35 * var6)) {
                        var4 = 2;
                     } else if (var7 >= Math.round(0.1 * var8)) {
                        var4 = var3;
                     } else {
                        var4 = 0;
                     }
                  default:
                     break label126;
               }
            case 2:
               switch (this.getCabalHighestScore()) {
                  case 0:
                     if (var5 >= Math.round(0.1 * var6)) {
                        var4 = var3;
                     } else if (var7 >= Math.round(0.35 * var8)) {
                        var4 = 1;
                     } else {
                        var4 = 0;
                     }
                     break;
                  case 1:
                     if (var7 >= Math.round(0.1 * var8)) {
                        var4 = 1;
                     } else if (var5 >= Math.round(0.35 * var6)) {
                        var4 = var3;
                     } else {
                        var4 = 0;
                     }
                     break;
                  case 2:
                     if (var5 >= Math.round(0.1 * var6)) {
                        var4 = var3;
                     } else if (var7 >= Math.round(0.35 * var8)) {
                        var4 = 1;
                     } else {
                        var4 = 0;
                     }
               }
         }

         this.PR.put(var2, var4);
         if (Config.SEND_SSQ_SEAL_STATUS) {
            switch (var2) {
               case 1:
                  if (var4 == 2) {
                     this.sendMessageToAll(1212);
                  } else if (var4 == 1) {
                     this.sendMessageToAll(1215);
                  }
                  break;
               case 2:
                  if (var4 == 2) {
                     this.sendMessageToAll(1213);
                  } else if (var4 == 1) {
                     this.sendMessageToAll(1216);
                  }
                  break;
               case 3:
                  if (var4 == 2) {
                     this.sendMessageToAll(1214);
                  } else if (var4 == 1) {
                     this.sendMessageToAll(1217);
                  }
            }
         }
      }
   }

   public int getPriestCabal(int var1) {
      switch (var1) {
         case 31078:
         case 31079:
         case 31080:
         case 31081:
         case 31082:
         case 31083:
         case 31084:
         case 31168:
         case 31692:
         case 31694:
         case 31997:
            return 2;
         case 31085:
         case 31086:
         case 31087:
         case 31088:
         case 31089:
         case 31090:
         case 31091:
         case 31169:
         case 31693:
         case 31695:
         case 31998:
            return 1;
         default:
            return 0;
      }
   }

   public void changePeriod() {
      this.PO = ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsPeriodChange(), 10L);
   }

   public void changePeriod(int var1) {
      this.changePeriod(var1, 1);
   }

   public void changePeriod(int var1, int var2) {
      this._activePeriod = var1 - 1;
      if (this._activePeriod < 0) {
         this._activePeriod += 4;
      }

      this.PO = ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsPeriodChange(), var2 * 1000L);
   }

   public void setTimeToNextPeriodChange(int var1) {
      this.PP.setTimeInMillis(System.currentTimeMillis() + var1 * 1000L * 60L);
      if (this.PO != null) {
         this.PO.cancel(false);
      }

      this.PO = ThreadPoolManager.getInstance().schedule(new SevenSigns.SevenSignsPeriodChange(), this.getMilliToPeriodChange());
   }

   public SevenSigns.SSListenerList getListenerEngine() {
      return this.PU;
   }

   public <T extends GameListener> boolean addListener(T var1) {
      return this.PU.add(var1);
   }

   public <T extends GameListener> boolean removeListener(T var1) {
      return this.PU.remove(var1);
   }

   private class OnStartListenerImpl implements OnStartListener {
      private OnStartListenerImpl() {
      }

      @Override
      public void onStart() {
         SevenSigns.this.getListenerEngine().onPeriodChange();
      }
   }

   protected class SSListenerList extends ListenerList<GameServer> {
      public void onPeriodChange() {
         if (SevenSigns.getInstance().getCurrentPeriod() == 3) {
            SevenSigns.getInstance().getCabalHighestScore();
         }

         this.forEachListener(OnSSPeriodListener.class, var0 -> var0.onPeriodChange(SevenSigns.getInstance().getCurrentPeriod()));
      }
   }

   public class SevenSignsAnnounce extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         if (Config.SEND_SSQ_WELCOME_MESSAGE) {
            for (Player var2 : GameObjectsStorage.getAllPlayersForIterate()) {
               SevenSigns.this.sendCurrentPeriodMsg(var2);
            }

            ThreadPoolManager.getInstance().schedule(SevenSigns.this.new SevenSignsAnnounce(), Config.SS_ANNOUNCE_PERIOD * 1000L * 60L);
         }
      }
   }

   public class SevenSignsPeriodChange extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         _log.info("SevenSignsPeriodChange: old=" + SevenSigns.this._activePeriod);
         int var1 = SevenSigns.this._activePeriod++;
         switch (var1) {
            case 0:
               SevenSigns.this.sendMessageToAll(1210);
               RaidBossSpawnManager.getInstance().distributeRewards();
               break;
            case 1:
               SevenSigns.this.sendMessageToAll(1211);
               int var2 = SevenSigns.this.getCabalHighestScore();
               SevenSigns.this.calcNewSealOwners();
               if (var2 == 1) {
                  SevenSigns.this.sendMessageToAll(1240);
               } else {
                  SevenSigns.this.sendMessageToAll(1241);
               }

               SevenSigns.this._previousWinner = var2;
               break;
            case 2:
               SevenSignsFestival.getInstance().distribAccumulatedBonus();
               SevenSignsFestival.getInstance().rewardHighestRanked();
               SevenSigns.this.initializeSeals();
               RaidBossSpawnManager.getInstance().distributeRewards();
               SevenSigns.this.sendMessageToAll(1218);
               _log.info(
                  "SevenSigns: The "
                     + SevenSigns.getCabalName(SevenSigns.this._previousWinner)
                     + " have won the competition with "
                     + SevenSigns.this.getCurrentScore(SevenSigns.this._previousWinner)
                     + " points!"
               );
               break;
            case 3:
               SevenSigns.this._activePeriod = 0;
               SevenSigns.this.sendMessageToAll(1219);
               SevenSigns.this.resetPlayerData();
               SevenSigns.this.resetSeals();
               SevenSigns.this._dawnStoneScore = 0L;
               SevenSigns.this._duskStoneScore = 0L;
               SevenSigns.this._dawnFestivalScore = 0L;
               SevenSigns.this._duskFestivalScore = 0L;
               SevenSigns.this._currentCycle++;
               SevenSignsFestival.getInstance().resetFestivalData(false);
         }

         SevenSigns.this.saveSevenSignsData(0, true);
         _log.info("SevenSignsPeriodChange: new=" + SevenSigns.this._activePeriod);

         try {
            _log.info("SevenSigns: Change Catacomb spawn...");
            SevenSigns.this.getListenerEngine().onPeriodChange();
            SSQInfo var6 = new SSQInfo();

            for (Player var4 : GameObjectsStorage.getAllPlayersForIterate()) {
               var4.sendPacket(var6);
            }

            _log.info("SevenSigns: Spawning NPCs...");
            _log.info("SevenSigns: The " + SevenSigns.this.getCurrentPeriodName() + " period has begun!");
            _log.info("SevenSigns: Calculating next period change time...");
            SevenSigns.this.setCalendarForNextPeriodChange();
            _log.info("SevenSignsPeriodChange: time to next change=" + Util.formatTime((int)(SevenSigns.this.getMilliToPeriodChange() / 1000L)));
            SevenSigns.SevenSignsPeriodChange var7 = SevenSigns.this.new SevenSignsPeriodChange();
            SevenSigns.this.PO = ThreadPoolManager.getInstance().schedule(var7, SevenSigns.this.getMilliToPeriodChange());
         } catch (Exception var5) {
            _log.error("", var5);
         }
      }
   }
}
