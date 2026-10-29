package l2.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.Future;
import l2.commons.collections.MultiValueSet;
import l2.commons.dao.JdbcEntityState;
import l2.commons.lang.ArrayUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.NextTime;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.CastleDamageZoneDAO;
import l2.gameserver.dao.CastleDoorUpgradeDAO;
import l2.gameserver.dao.CastleHiredGuardDAO;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.events.objects.DoorObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2.gameserver.model.entity.events.objects.SpawnExObject;
import l2.gameserver.model.entity.events.objects.SpawnSimpleObject;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.taskmanager.DelayedItemsManager;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.support.MerchantGuard;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.TeleportUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.TreeIntSet;

public class CastleSiegeEvent extends SiegeEvent<Castle, SiegeClanObject> {
   public static final int MAX_SIEGE_CLANS = 20;
   public static final long DAY_IN_MILISECONDS = 86400000L;
   public static final String DEFENDERS_WAITING = "defenders_waiting";
   public static final String DEFENDERS_REFUSED = "defenders_refused";
   public static final String CONTROL_TOWERS = "control_towers";
   public static final String FLAME_TOWERS = "flame_towers";
   public static final String BOUGHT_ZONES = "bought_zones";
   public static final String GUARDS = "guards";
   public static final String HIRED_GUARDS = "hired_guards";
   private IntSet QF = Containers.EMPTY_INT_SET;
   private Future<?> QG = null;
   private boolean QH = false;
   private NextTime[] QI = new NextTime[0];
   private long QJ;
   private Pair<ItemTemplate, Long> QK = null;
   private Pair<ItemTemplate, Long> QL = null;

   public CastleSiegeEvent(MultiValueSet<String> var1) {
      super(var1);
      String var2 = var1.getString("siege_schedule");
      StringTokenizer var3 = new StringTokenizer(var2, "|;");

      while (var3.hasMoreTokens()) {
         this.QI = ArrayUtils.add(this.QI, new SchedulingPattern(var3.nextToken()));
      }

      String var4 = var1.getString("on_siege_end_attacker_owned_leader_reward", null);
      if (var4 != null) {
         String[] var5 = var4.split(":");
         this.QK = Pair.of(ItemHolder.getInstance().getTemplate(Integer.parseInt(var5[0])), Long.valueOf(var5[1]));
      }

      String var7 = var1.getString("on_siege_end_defender_owned_leader_reward", null);
      if (var7 != null) {
         String[] var6 = var7.split(":");
         this.QL = Pair.of(ItemHolder.getInstance().getTemplate(Integer.parseInt(var6[0])), Long.valueOf(var6[1]));
      }

      this.QJ = var1.getLong("next_siege_date_set_delay", 86400L) * 1000L;
   }

   @Override
   public void initEvent() {
      super.initEvent();
      List var1 = this.getObjects("doors");
      this.addObjects("bought_zones", CastleDamageZoneDAO.getInstance().load(this.getResidence()));

      for (Object __raw_var3_105 : (Iterable<?>)(Object)(var1)) {
         DoorObject var3 = (DoorObject)__raw_var3_105;
         var3.setUpgradeValue(this, CastleDoorUpgradeDAO.getInstance().load(var3.getUId()));
         var3.getDoor().addListener(this._doorDeathListener);
      }
   }

   @Override
   public void processStep(Clan var1) {
      Clan var2 = this.getResidence().getOwner();
      this.getResidence().changeOwner(var1);
      if (var2 != null) {
         SiegeClanObject var3 = this.getSiegeClan("defenders", var2);
         this.removeObject("defenders", var3);
         var3.setType("attackers");
         this.addObject("attackers", var3);
      } else {
         if (this.getObjects("attackers").size() == 1) {
            this.stopEvent();
            return;
         }

         int var8 = var1.getAllyId();
         if (var8 > 0) {
            List var4 = this.getObjects("attackers");
            boolean var5 = true;

            for (Object __raw_var7_131 : (Iterable<?>)(Object)(var4)) {
               SiegeClanObject var7 = (SiegeClanObject)__raw_var7_131;
               if (var7 != null && var7.getClan().getAllyId() != var8) {
                  var5 = false;
               }
            }

            if (var5) {
               this.stopEvent();
               return;
            }
         }
      }

      SiegeClanObject var9 = this.getSiegeClan("attackers", var1);
      var9.deleteFlag();
      var9.setType("defenders");
      this.removeObject("attackers", var9);
      List var10 = this.removeObjects("defenders");

      for (Object __raw_var13_150 : (Iterable<?>)(Object)(var10)) {
         SiegeClanObject var13 = (SiegeClanObject)__raw_var13_150;
         var13.setType("attackers");
      }

      this.addObject("defenders", var9);
      this.addObjects("attackers", var10);
      this.updateParticles(true, "attackers", "defenders");
      this.teleportPlayers("attackers");
      this.teleportPlayers("spectators");
      if (!this.QH) {
         this.QH = true;
         this.broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED, "attackers", "defenders");
         if (this._oldOwner != null) {
            this.spawnAction("hired_guards", false);
            this.e(false);
            this.removeObjects("hired_guards");
            this.removeObjects("bought_zones");
            CastleDamageZoneDAO.getInstance().delete(this.getResidence());
         } else {
            this.spawnAction("guards", false);
         }

         for (Object __raw_var15_172 : (Iterable<?>)(Object)(this.getObjects("doors"))) {
            DoorObject var15 = (DoorObject)__raw_var15_172;
            var15.setWeak(true);
            var15.setUpgradeValue(this, 0);
            CastleDoorUpgradeDAO.getInstance().delete(var15.getUId());
         }
      }

      this.spawnAction("doors", true);
      this.spawnAction("control_towers", true);
      this.spawnAction("flame_towers", true);
      this.despawnSiegeSummons();
   }

   @Override
   public void startEvent() {
      this._oldOwner = this.getResidence().getOwner();
      if (this._oldOwner != null) {
         this.addObject("defenders", new SiegeClanObject("defenders", this._oldOwner, 0L));
         if (this.getResidence().getSpawnMerchantTickets().size() > 0) {
            for (ItemInstance var2 : this.getResidence().getSpawnMerchantTickets()) {
               MerchantGuard var3 = this.getResidence().getMerchantGuard(var2.getItemId());
               this.addObject("hired_guards", new SpawnSimpleObject(var3.getNpcId(), var2.getLoc()));
               var2.deleteMe();
            }

            CastleHiredGuardDAO.getInstance().delete(this.getResidence());
            this.spawnAction("hired_guards", true);
         }
      }

      List var4 = this.getObjects("attackers");
      if (var4.isEmpty()) {
         if (this._oldOwner == null) {
            this.broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName(this.getResidence()));
         } else {
            this.broadcastToWorld(
               new SystemMessage2(SystemMsg.S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED).addResidenceName(this.getResidence())
            );
         }

         this.reCalcNextTime(false);
      } else {
         SiegeClanDAO.getInstance().delete(this.getResidence());
         this.updateParticles(true, "attackers", "defenders");
         this.broadcastTo(SystemMsg.THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT, "attackers");
         super.startEvent();
         if (this._oldOwner == null) {
            this.cJ();
         } else {
            this.e(true);
         }
      }
   }

   @Override
   public void stopEvent(boolean var1) {
      for (Object __raw_var4_228 : (Iterable<?>)(Object)(this.getObjects("doors"))) {
         DoorObject var4 = (DoorObject)__raw_var4_228;
         var4.setWeak(false);
      }

      this.e(false);
      this.updateParticles(false, "attackers", "defenders");

      for (Object __raw_var5_235 : (Iterable<?>)(Object)(this.removeObjects("attackers"))) {
         SiegeClanObject var5 = (SiegeClanObject)__raw_var5_235;
         var5.deleteFlag();
      }

      this.broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(this.getResidence()));
      this.removeObjects("defenders");
      this.removeObjects("defenders_waiting");
      this.removeObjects("defenders_refused");
      Clan var12 = this.getResidence().getOwner();
      if (var12 != null) {
         if (this._oldOwner == var12) {
            var12.broadcastToOnlineMembers(
               new SystemMessage2(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)
                  .addInteger(var12.incReputation(Config.SIEGE_CLAN_REPUTATION_REWARD, false, this.toString()))
            );
            if (Config.EVENT_CLAN_CASTLE_POINTS.containsKey(this.getResidence().getId())) {
               var12.setCustomPoints(var12.getCustomPoints() + Config.EVENT_CLAN_CASTLE_POINTS.get(this.getResidence().getId()));
               if (Config.SERVICES_TOP_CLANS_STATISTIC_ANNOUNCE) {
                  Announcements.getInstance()
                     .announceByCustomMessage(
                        "scripts.services.CustomClanPointsCastle",
                        new String[]{String.valueOf(var12.getName()), String.valueOf(Config.EVENT_CLAN_CASTLE_POINTS.get(this.getResidence().getId()))}
                     );
               }
            }

            UnitMember var14 = var12.getLeader();
            if (var14 != null && this.QL != null) {
               int var16 = ((ItemTemplate)this.QL.getLeft()).getItemId();
               long var18 = (Long)this.QL.getRight();
               Player var19 = World.getPlayer(var14.getObjectId());
               if (var19 != null && var19.isOnline()) {
                  ItemFunctions.addItem(var19, var16, var18, true);
                  Log.LogItem(var19, Log.ItemLog.PostSend, var16, var18);
               } else {
                  DelayedItemsManager.getInstance()
                     .addDelayed(var14.getObjectId(), var16, (int)var18, 0, "End siege owner leader reward item " + var16 + "(" + var18 + ")");
               }
            }
         } else {
            this.broadcastToWorld(
               new SystemMessage2(SystemMsg.CLAN_S1_IS_VICTORIOUS_OVER_S2S_CASTLE_SIEGE).addString(var12.getName()).addResidenceName(this.getResidence())
            );
            var12.broadcastToOnlineMembers(
               new SystemMessage2(SystemMsg.SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE)
                  .addInteger(var12.incReputation(Config.SIEGE_CLAN_REPUTATION_DEFEND_REWARD, false, this.toString()))
            );
            if (Config.EVENT_CLAN_CASTLE_POINTS.containsKey(this.getResidence().getId())) {
               var12.setCustomPoints(var12.getCustomPoints() + Config.EVENT_CLAN_CASTLE_POINTS.get(this.getResidence().getId()));
               Announcements.getInstance()
                  .announceByCustomMessage(
                     "scripts.services.CustomClanPointsCastle",
                     new String[]{String.valueOf(var12.getName()), String.valueOf(Config.EVENT_CLAN_CASTLE_POINTS.get(this.getResidence().getId()))}
                  );
            }

            if (this._oldOwner != null) {
               this._oldOwner
                  .broadcastToOnlineMembers(
                     new SystemMessage2(
                           SystemMsg.YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOU_CLAN_REPUTATION_SCORE_AND_ADDED_TO_YOUR_OPPONENTS
                        )
                        .addInteger(-this._oldOwner.incReputation(Config.SIEGE_CLAN_REPUTATION_DEFEND_CONSUME, false, this.toString()))
                  );
            }

            UnitMember var13 = var12.getLeader();

            for (UnitMember var7 : var12) {
               Player var8 = var7.getPlayer();
               if (var8 != null) {
                  var8.sendPacket(PlaySound.SIEGE_VICTORY);
                  if (var8.isOnline() && var8.isNoble()) {
                     HeroController.getInstance().addHeroDiary(var8.getObjectId(), 3, this.getResidence().getId());
                  }
               }
            }

            if (this.QK != null) {
               int var15 = ((ItemTemplate)this.QK.getLeft()).getItemId();
               long var17 = (Long)this.QK.getRight();
               Player var9 = World.getPlayer(var13.getObjectId());
               if (var9 != null && var9.isOnline()) {
                  ItemFunctions.addItem(var9, var15, var17, true);
                  Log.LogItem(var9, Log.ItemLog.PostSend, var15, var17);
               } else {
                  DelayedItemsManager.getInstance()
                     .addDelayed(var13.getObjectId(), var15, (int)var17, 0, "End siege owner leader reward item " + var15 + "(" + var17 + ")");
               }
            }
         }

         this.getResidence().getOwnDate().setTimeInMillis(System.currentTimeMillis());
         this.getResidence().getLastSiegeDate().setTimeInMillis(this.getResidence().getSiegeDate().getTimeInMillis());
      } else {
         this.broadcastToWorld(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(this.getResidence()));
         this.getResidence().getOwnDate().setTimeInMillis(0L);
         this.getResidence().getLastSiegeDate().setTimeInMillis(0L);
      }

      this.despawnSiegeSummons();
      if (this._oldOwner != null) {
         this.spawnAction("hired_guards", false);
         this.removeObjects("hired_guards");
      }

      super.stopEvent(var1);
   }

   @Override
   public void reCalcNextTime(boolean var1) {
      this.clearActions();
      long var2 = System.currentTimeMillis();
      Calendar var4 = this.getResidence().getSiegeDate();
      Calendar var5 = this.getResidence().getOwnDate();
      if (var1) {
         if (var4.getTimeInMillis() > var2) {
            this.registerActions();
         } else if (var4.getTimeInMillis() == 0L) {
            if (var2 - var5.getTimeInMillis() > this.QJ) {
               this.cK();
            } else {
               this.generateNextSiegeDates();
            }
         } else if (var4.getTimeInMillis() <= var2) {
            this.cK();
         }
      } else if (this.getResidence().getOwner() != null) {
         this.getResidence().getSiegeDate().setTimeInMillis(0L);
         this.getResidence().setJdbcState(JdbcEntityState.UPDATED);
         this.getResidence().update();
         this.generateNextSiegeDates();
      } else {
         this.cK();
      }
   }

   @Override
   public void loadSiegeClans() {
      super.loadSiegeClans();
      this.addObjects("defenders_waiting", SiegeClanDAO.getInstance().load(this.getResidence(), "defenders_waiting"));
      this.addObjects("defenders_refused", SiegeClanDAO.getInstance().load(this.getResidence(), "defenders_refused"));
   }

   @Override
   public void setRegistrationOver(boolean var1) {
      if (var1) {
         this.broadcastToWorld(new SystemMessage2(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED).addResidenceName(this.getResidence()));
      }

      super.setRegistrationOver(var1);
   }

   @Override
   public void announce(int var1) {
      int var3 = var1 / 60;
      int var4 = var3 / 60;
      SystemMessage2 var2;
      if (var4 > 0) {
         var2 = new SystemMessage2(SystemMsg.S1_HOURS_UNTIL_CASTLE_SIEGE_CONCLUSION).addInteger(var4);
      } else if (var3 > 0) {
         var2 = new SystemMessage2(SystemMsg.S1_MINUTES_UNTIL_CASTLE_SIEGE_CONCLUSION).addInteger(var3);
      } else {
         var2 = new SystemMessage2(SystemMsg.THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECONDS).addInteger(var1);
      }

      this.broadcastTo(var2, "attackers", "defenders");
   }

   private void cJ() {
      List var1 = this.getObjects("guards");
      ArrayList var2 = new ArrayList();

      for (Object __raw_var4_408 : (Iterable<?>)(Object)(var1)) {
         SpawnExObject var4 = (SpawnExObject)__raw_var4_408;
         var2.addAll(var4.getSpawns());
      }

      List var15 = this.getObjects("control_towers");

      for (Object __raw_var10_414 : (Iterable<?>)(Object)(var2)) {
         Spawner var10 = (Spawner)__raw_var10_414;
         Location var11 = var10.getCurrentSpawnRange().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex());
         SiegeToggleNpcInstance var16 = null;
         double var7 = 0.0;

         for (Object __raw_var13_419 : (Iterable<?>)(Object)(var15)) {
            SiegeToggleNpcObject var13 = (SiegeToggleNpcObject)__raw_var13_419;
            SiegeToggleNpcInstance var14 = var13.getToggleNpc();
            double var5 = var14.getDistance(var11);
            if (var16 == null || var5 < var7) {
               var16 = var14;
               var7 = var5;
            }

            var16.register(var10);
         }
      }
   }

   private void e(boolean var1) {
      this.zoneAction("bought_zones", var1);
   }

   private void cK() {
      Calendar var1 = (Calendar)Config.CASTLE_VALIDATION_DATE.clone();
      var1.set(7, 1);
      var1.set(11, this.getResidence().getLastSiegeDate().get(11));
      if (var1.before(Config.CASTLE_VALIDATION_DATE)) {
         var1.add(3, 1);
      }

      var1.setTimeInMillis(this.scheduleNextTime(var1.getTimeInMillis(), this.QI[0]));
      this.l(var1.getTimeInMillis());
   }

   public void generateNextSiegeDates() {
      if (this.getResidence().getSiegeDate().getTimeInMillis() == 0L) {
         Calendar var1 = (Calendar)Config.CASTLE_VALIDATION_DATE.clone();
         var1.set(7, 1);
         if (var1.before(Config.CASTLE_VALIDATION_DATE)) {
            var1.add(3, 1);
         }

         this.QF = new TreeIntSet();

         for (NextTime var5 : this.QI) {
            this.QF.add((int)(this.scheduleNextTime(var1.getTimeInMillis(), var5) / 1000L));
         }

         long var6 = this.getResidence().getOwnDate().getTimeInMillis() + this.QJ - System.currentTimeMillis();
         this.QG = ThreadPoolManager.getInstance().schedule(new CastleSiegeEvent.NextSiegeDateSet(), var6);
      }
   }

   protected long scheduleNextTime(long var1, NextTime var3) {
      GregorianCalendar var4 = new GregorianCalendar(TimeZone.getDefault());
      var4.setTimeInMillis(var1);
      var4.set(13, 0);
      var4.set(14, 0);
      long var5 = var4.getTimeInMillis();

      while (var5 < System.currentTimeMillis()) {
         var5 = var3.next(var5);
      }

      return var5;
   }

   public void setNextSiegeTime(int var1) {
      if (this.QF.contains(var1) && this.QG != null) {
         this.QF = Containers.EMPTY_INT_SET;
         this.QG.cancel(false);
         this.QG = null;
         this.l(var1 * 1000L);
      }
   }

   private void l(long var1) {
      this.broadcastToWorld(new SystemMessage2(SystemMsg.S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME).addResidenceName(this.getResidence()));
      this.clearActions();
      this.getResidence().getSiegeDate().setTimeInMillis(var1);
      this.getResidence().setJdbcState(JdbcEntityState.UPDATED);
      this.getResidence().update();
      this.registerActions();
   }

   @Override
   public boolean isAttackersInAlly() {
      return !this.QH;
   }

   public int[] getNextSiegeTimes() {
      return this.QF.toArray();
   }

   @Override
   public boolean canResurrect(Player var1, Creature var2, boolean var3) {
      boolean var4 = var1.isInZone(Zone.ZoneType.SIEGE);
      boolean var5 = var2.isInZone(Zone.ZoneType.SIEGE);
      if (!var4 && !var5) {
         return true;
      }

      if (!var5) {
         return false;
      }

      Player var6 = var2.getPlayer();
      CastleSiegeEvent var7 = var2.getEvent(CastleSiegeEvent.class);
      if (var7 != this) {
         if (var3) {
            var6.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
         }

         var1.sendPacket(var3 ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
         return false;
      } else {
         SiegeClanObject var8 = var7.getSiegeClan("attackers", var6.getClan());
         if (var8 == null) {
            var8 = var7.getSiegeClan("defenders", var6.getClan());
         }

         if (var8.getType() == "attackers") {
            if (var8.getFlag() == null) {
               if (var3) {
                  var6.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
               }

               var1.sendPacket(var3 ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
               return false;
            }
         } else {
            List var9 = this.getObjects("control_towers");
            int var10 = 0;

            for (Object __raw_var12_548 : (Iterable<?>)(Object)(var9)) {
               SiegeToggleNpcObject var12 = (SiegeToggleNpcObject)__raw_var12_548;
               if (!var12.isAlive()) {
                  var10++;
               }
            }

            if (var10 > 1) {
               if (var3) {
                  var6.sendPacket(SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE);
               }

               var1.sendPacket(var3 ? SystemMsg.THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET);
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public Location getRestartLoc(Player var1, RestartType var2) {
      Location var3;
      switch (var2) {
         case TO_VILLAGE:
            var3 = TeleportUtils.getRestartLocation(var1, RestartType.TO_VILLAGE);
            break;
         default:
            var3 = super.getRestartLoc(var1, var2);
      }

      return var3;
   }

   private class NextSiegeDateSet extends RunnableImpl {
      private NextSiegeDateSet() {
      }

      @Override
      public void runImpl() throws Exception {
         CastleSiegeEvent.this.cK();
      }
   }
}
