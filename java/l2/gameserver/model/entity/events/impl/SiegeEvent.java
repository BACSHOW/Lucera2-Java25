package l2.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import l2.commons.collections.LazyArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.dao.JdbcEntityState;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.Config;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.TimeUtils;

public abstract class SiegeEvent<R extends Residence, S extends SiegeClanObject> extends GlobalEvent {
   public static final String OWNER = "owner";
   public static final String OLD_OWNER = "old_owner";
   public static final String ATTACKERS = "attackers";
   public static final String DEFENDERS = "defenders";
   public static final String SPECTATORS = "spectators";
   public static final String SIEGE_ZONES = "siege_zones";
   public static final String FLAG_ZONES = "flag_zones";
   public static final String DAY_OF_WEEK = "day_of_week";
   public static final String HOUR_OF_DAY = "hour_of_day";
   public static final String REGISTRATION = "registration";
   public static final String DOORS = "doors";
   protected R _residence;
   private boolean QQ;
   private boolean QR;
   protected int _dayOfWeek;
   protected int _hourOfDay;
   protected Clan _oldOwner;
   protected OnDeathListener _doorDeathListener = new SiegeEvent.DoorDeathListener();
   protected List<HardReference<SummonInstance>> _siegeSummons = new ArrayList<>();

   public SiegeEvent(MultiValueSet<String> var1) {
      super(var1);
      this._dayOfWeek = var1.getInteger("day_of_week", 0);
      this._hourOfDay = var1.getInteger("hour_of_day", 0);
   }

   @Override
   public void startEvent() {
      this.setInProgress(true);
      super.startEvent();
   }

   @Override
   public final void stopEvent() {
      this.stopEvent(false);
   }

   public void stopEvent(boolean var1) {
      this.despawnSiegeSummons();
      this.setInProgress(false);
      this.reCalcNextTime(false);
      super.stopEvent();
   }

   public void processStep(Clan var1) {
   }

   @Override
   public void reCalcNextTime(boolean var1) {
      this.clearActions();
      Calendar var2 = this.getResidence().getSiegeDate();
      if (var1) {
         if (var2.getTimeInMillis() <= System.currentTimeMillis()) {
            var2.set(7, this._dayOfWeek);
            var2.set(11, this._hourOfDay);
            this.validateSiegeDate(var2, 2);
            this.getResidence().setJdbcState(JdbcEntityState.UPDATED);
         }
      } else {
         var2.add(3, 2);
         this.getResidence().setJdbcState(JdbcEntityState.UPDATED);
      }

      this.registerActions();
      this.getResidence().update();
   }

   protected void validateSiegeDate(Calendar var1, int var2) {
      var1.set(12, 0);
      var1.set(13, 0);
      var1.set(14, 0);

      while (var1.getTimeInMillis() < System.currentTimeMillis()) {
         var1.add(3, var2);
      }
   }

   @Override
   protected long startTimeMillis() {
      return this.getResidence().getSiegeDate().getTimeInMillis();
   }

   @Override
   public void teleportPlayers(String var1) {
      List var2 = new ArrayList();
      Clan var3 = this.getResidence().getOwner();
      if (var1.equalsIgnoreCase("owner")) {
         if (var3 != null) {
            for (Player var5 : this.getPlayersInZone()) {
               if (var5.getClan() == var3) {
                  var2.add(var5);
               }
            }
         }
      } else if (var1.equalsIgnoreCase("attackers")) {
         for (Player var11 : this.getPlayersInZone()) {
            SiegeClanObject var6 = this.getSiegeClan("attackers", var11.getClan());
            if (var6 != null && var6.isParticle(var11)) {
               var2.add(var11);
            }
         }
      } else if (var1.equalsIgnoreCase("defenders")) {
         for (Player var12 : this.getPlayersInZone()) {
            if (var3 == null || var12.getClan() == null || var12.getClan() != var3) {
               SiegeClanObject var15 = this.getSiegeClan("defenders", var12.getClan());
               if (var15 != null && var15.isParticle(var12)) {
                  var2.add(var12);
               }
            }
         }
      } else if (var1.equalsIgnoreCase("spectators")) {
         for (Player var13 : this.getPlayersInZone()) {
            if ((var3 == null || var13.getClan() == null || var13.getClan() != var3)
               && (
                  var13.getClan() == null || this.getSiegeClan("attackers", var13.getClan()) == null && this.getSiegeClan("defenders", var13.getClan()) == null
               )) {
               var2.add(var13);
            }
         }
      } else {
         var2 = this.getPlayersInZone();
      }

      for (Object __raw_var14_159 : (Iterable<?>)(Object)(var2)) {
         Player var14 = (Player)__raw_var14_159;
         Location var16 = null;
         if (!var1.equalsIgnoreCase("owner") && !var1.equalsIgnoreCase("defenders")) {
            var16 = this.getResidence().getNotOwnerRestartPoint(var14);
         } else {
            var16 = this.getResidence().getOwnerRestartPoint();
         }

         var14.teleToLocation(var16, ReflectionManager.DEFAULT);
      }
   }

   public List<Player> getPlayersInZone() {
      List var1 = this.getObjects("siege_zones");
      LazyArrayList var2 = new LazyArrayList();

      for (Object __raw_var4_175 : (Iterable<?>)(Object)(var1)) {
         ZoneObject var4 = (ZoneObject)__raw_var4_175;
         var2.addAll(var4.getInsidePlayers());
      }

      return var2;
   }

   public void broadcastInZone(L2GameServerPacket... var1) {
      for (Player var3 : this.getPlayersInZone()) {
         var3.sendPacket(var1);
      }
   }

   public void broadcastInZone(IStaticPacket... var1) {
      for (Player var3 : this.getPlayersInZone()) {
         var3.sendPacket(var1);
      }
   }

   public boolean checkIfInZone(Creature var1) {
      for (Object __raw_var4_195 : (Iterable<?>)(Object)(this.getObjects("siege_zones"))) {
         ZoneObject var4 = (ZoneObject)__raw_var4_195;
         if (var4.checkIfInZone(var1)) {
            return true;
         }
      }

      return false;
   }

   public void broadcastInZone2(IStaticPacket... var1) {
      for (Player var3 : this.getResidence().getZone().getInsidePlayers()) {
         var3.sendPacket(var1);
      }
   }

   public void broadcastInZone2(L2GameServerPacket... var1) {
      for (Player var3 : this.getResidence().getZone().getInsidePlayers()) {
         var3.sendPacket(var1);
      }
   }

   public void loadSiegeClans() {
      this.addObjects("attackers", SiegeClanDAO.getInstance().load(this.getResidence(), "attackers"));
      this.addObjects("defenders", SiegeClanDAO.getInstance().load(this.getResidence(), "defenders"));
   }

   public S newSiegeClan(String var1, int var2, long var3, long var5) {
      Clan var7 = ClanTable.getInstance().getClan(var2);
      return (S)(var7 == null ? null : new SiegeClanObject(var1, var7, var3, var5));
   }

   public void updateParticles(boolean var1, String... var2) {
      for (String var6 : var2) {
         for (Object __raw_var9_228 : (Iterable<?>)(Object)(this.getObjects(var6))) {
            SiegeClanObject var9 = (SiegeClanObject)__raw_var9_228;
            var9.setEvent(var1, this);
         }
      }
   }

   public S getSiegeClan(String var1, Clan var2) {
      return var2 == null ? null : this.getSiegeClan(var1, var2.getClanId());
   }

   public S getSiegeClan(String var1, int var2) {
      List var3 = this.getObjects(var1);
      if (var3.isEmpty()) {
         return null;
      }

      for (int var4 = 0; var4 < var3.size(); var4++) {
         SiegeClanObject var5 = (SiegeClanObject)var3.get(var4);
         if (var5.getObjectId() == var2) {
            return (S)var5;
         }
      }

      return null;
   }

   public void broadcastTo(IStaticPacket var1, String... var2) {
      for (String var6 : var2) {
         for (Object __raw_var9_256 : (Iterable<?>)(Object)(this.getObjects(var6))) {
            SiegeClanObject var9 = (SiegeClanObject)__raw_var9_256;
            var9.broadcast(var1);
         }
      }
   }

   public void broadcastTo(L2GameServerPacket var1, String... var2) {
      for (String var6 : var2) {
         for (Object __raw_var9_264 : (Iterable<?>)(Object)(this.getObjects(var6))) {
            SiegeClanObject var9 = (SiegeClanObject)__raw_var9_264;
            var9.broadcast(var1);
         }
      }
   }

   @Override
   public void initEvent() {
      this._residence = ResidenceHolder.getInstance().getResidence(this.getId());
      this.loadSiegeClans();
      this.clearActions();
      super.initEvent();
   }

   @Override
   protected void printInfo() {
      long var1 = this.startTimeMillis();
      if (var1 == 0L) {
         this.info(this.getName() + " time - undefined");
      } else {
         this.info(this.getName() + " time - " + TimeUtils.toSimpleFormat(var1));
      }
   }

   @Override
   public boolean ifVar(String var1) {
      if (var1.equals("owner")) {
         return this.getResidence().getOwner() != null;
      } else {
         return var1.equals("old_owner") ? this._oldOwner != null : false;
      }
   }

   @Override
   public boolean isParticle(Player var1) {
      return this.isInProgress() && var1.getClan() != null
         ? this.getSiegeClan("attackers", var1.getClan()) != null || this.getSiegeClan("defenders", var1.getClan()) != null
         : false;
   }

   @Override
   public void checkRestartLocs(Player var1, Map<RestartType, Boolean> var2) {
      if (!this.getObjects("flag_zones").isEmpty()) {
         SiegeClanObject var3 = this.getSiegeClan("attackers", var1.getClan());
         if (var3 != null && var3.getFlag() != null) {
            var2.put(RestartType.TO_FLAG, Boolean.TRUE);
         }
      }
   }

   @Override
   public Location getRestartLoc(Player var1, RestartType var2) {
      SiegeClanObject var3 = this.getSiegeClan("attackers", var1.getClan());
      Location var4 = null;
      switch (var2) {
         case TO_FLAG:
            if (!this.getObjects("flag_zones").isEmpty() && var3 != null && var3.getFlag() != null) {
               var4 = Location.findPointToStay(var3.getFlag(), 50, 75);
            } else {
               var1.sendPacket(SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
            }
         default:
            return var4;
      }
   }

   @Override
   public int getRelation(Player var1, Player var2, int var3) {
      Clan var4 = var1.getClan();
      Clan var5 = var2.getClan();
      if (var4 != null && var5 != null) {
         SiegeEvent var6 = var2.getEvent(SiegeEvent.class);
         if (this == var6) {
            var3 |= 512;
            SiegeClanObject var7 = this.getSiegeClan("attackers", var4);
            SiegeClanObject var8 = this.getSiegeClan("attackers", var5);
            if ((var7 != null || var8 != null) && (var7 == null || var8 == null || var7 != var8 && !this.isAttackersInAlly())) {
               var3 |= 4096;
            } else {
               var3 |= 2048;
            }

            if (var7 != null) {
               var3 |= 1024;
            }
         }

         return var3;
      } else {
         return var3;
      }
   }

   @Override
   public int getUserRelation(Player var1, int var2) {
      SiegeClanObject var3 = this.getSiegeClan("attackers", var1.getClan());
      if (var3 != null) {
         var2 |= 384;
      } else {
         var2 |= 128;
      }

      return var2;
   }

   @Override
   public SystemMsg checkForAttack(Creature var1, Creature var2, Skill var3, boolean var4) {
      SiegeEvent var5 = var1.getEvent(SiegeEvent.class);
      if (this != var5) {
         return null;
      }

      if (this.checkIfInZone(var1) && this.checkIfInZone(var2)) {
         Player var6 = var1.getPlayer();
         if (var6 == null) {
            return null;
         } else {
            SiegeClanObject var7 = this.getSiegeClan("attackers", var6.getClan());
            if (var7 == null && var2.isSiegeGuard()) {
               return SystemMsg.INVALID_TARGET;
            } else {
               Player var8 = var2.getPlayer();
               if (var8 == null) {
                  return SystemMsg.INVALID_TARGET;
               } else {
                  SiegeClanObject var9 = this.getSiegeClan("attackers", var8.getClan());
                  if (!Config.ALLOW_TEMPORARILY_ALLY_ON_FIRST_SIEGE
                     || (var7 != null || var9 != null) && (var7 == null || var9 == null || var7 != var9 && !this.isAttackersInAlly())) {
                     return var7 == null && var9 == null ? SystemMsg.INVALID_TARGET : null;
                  } else {
                     return SystemMsg.INVALID_TARGET;
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean isInProgress() {
      return this.QQ;
   }

   @Override
   public void action(String var1, boolean var2) {
      if (var1.equalsIgnoreCase("registration")) {
         this.setRegistrationOver(!var2);
      } else {
         super.action(var1, var2);
      }
   }

   public boolean isAttackersInAlly() {
      return false;
   }

   @Override
   public List<Player> broadcastPlayers(int var1) {
      return this.itemObtainPlayers();
   }

   @Override
   public List<Player> itemObtainPlayers() {
      List var1 = this.getPlayersInZone();
      LazyArrayList var2 = new LazyArrayList(var1.size());

      for (Player var4 : this.getPlayersInZone()) {
         if (var4.getEvent(this.getClass()) == this) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public Location getEnterLoc(Player var1) {
      SiegeClanObject var2 = this.getSiegeClan("attackers", var1.getClan());
      if (var2 != null) {
         return var2.getFlag() != null ? Location.findAroundPosition(var2.getFlag(), 50, 75) : this.getResidence().getNotOwnerRestartPoint(var1);
      } else {
         return this.getResidence().getOwnerRestartPoint();
      }
   }

   public R getResidence() {
      return this._residence;
   }

   public void setInProgress(boolean var1) {
      this.QQ = var1;
   }

   public boolean isRegistrationOver() {
      return this.QR;
   }

   public void setRegistrationOver(boolean var1) {
      this.QR = var1;
   }

   public void addSiegeSummon(SummonInstance var1) {
      this._siegeSummons.add(var1.getRef());
   }

   public boolean containsSiegeSummon(SummonInstance var1) {
      return this._siegeSummons.contains(var1.getRef());
   }

   public void despawnSiegeSummons() {
      for (HardReference var2 : this._siegeSummons) {
         SummonInstance var3 = (SummonInstance)var2.get();
         if (var3 != null) {
            var3.unSummon();
         }
      }

      this._siegeSummons.clear();
   }

   public class DoorDeathListener implements OnDeathListener {
      @Override
      public void onDeath(Creature var1, Creature var2) {
         if (SiegeEvent.this.isInProgress()) {
            DoorInstance var3 = (DoorInstance)var1;
            if (var3.getDoorType() != DoorTemplate.DoorType.WALL) {
               SiegeEvent.this.broadcastTo(SystemMsg.THE_CASTLE_GATE_HAS_BEEN_DESTROYED, "attackers", "defenders");
            }
         }
      }
   }
}
