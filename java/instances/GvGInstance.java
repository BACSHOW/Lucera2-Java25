package instances;

import events.GvG.GvG;
import gnu.trove.TIntObjectHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.mutable.MutableInt;

public class GvGInstance extends Reflection {
   private static final int mr = 25656;
   private static final int cp = 25655;
   private static final int ms = 20;
   private static final int mt = 100;
   private static final int mu = 5;
   private static final int mv = 3;
   private int mw = 1200;
   private long mx = 600000L;
   private boolean active = false;
   private Party my;
   private Party mz;
   private List<HardReference<Player>> mA = new CopyOnWriteArrayList<>();
   private TIntObjectHashMap<MutableInt> mB = new TIntObjectHashMap();
   private int mC = 0;
   private int mD = 0;
   private long startTime;
   private ScheduledFuture<?> mE;
   private ScheduledFuture<?> mF;
   private ScheduledFuture<?> mG;
   private GvGInstance.DeathListener mH = new GvGInstance.DeathListener();
   private GvGInstance.TeleportListener mI = new GvGInstance.TeleportListener();
   private GvGInstance.PlayerPartyLeaveListener mJ = new GvGInstance.PlayerPartyLeaveListener();
   private Zone mK;
   private Zone mL;
   private Zone mM;
   private Zone mN;
   private Zone mO;
   private Zone mP;

   public void setTeam1(Party var1) {
      this.my = var1;
   }

   public void setTeam2(Party var1) {
      this.mz = var1;
   }

   public void start() {
      this.mL = this.getZone("[gvg_battle_zone]");
      this.mN = this.getZone("[gvg_1_peace]");
      this.mP = this.getZone("[gvg_2_peace]");
      Location[] var1 = new Location[]{
         new Location(78568, 92408, -2440, 0),
         new Location(78664, 92440, -2440, 0),
         new Location(78760, 92488, -2440, 0),
         new Location(78872, 92264, -2440, 0),
         new Location(78696, 92168, -2440, 0),
         new Location(79624, 89736, -2440, 0),
         new Location(79576, 89830, -2440, 0),
         new Location(79534, 89917, -2440, 0),
         new Location(79743, 90028, -2440, 0)
      };

      for (int var2 = 0; var2 < var1.length; var2++) {
         this.addSpawnWithoutRespawn(25656, var1[var2], 0);
      }

      this.addSpawnWithoutRespawn(35423, new Location(79928, 88328, -2880), 0);
      this.addSpawnWithoutRespawn(35426, new Location(77016, 93080, -2880), 0);
      this.mE = ThreadPoolManager.getInstance().schedule(new GvGInstance.BossSpawn(), this.mx);
      this.mF = ThreadPoolManager.getInstance().schedule(new GvGInstance.CountingDown(), (this.mw - 1) * 1000L);
      this.mG = ThreadPoolManager.getInstance().schedule(new GvGInstance.BattleEnd(), (this.mw - 6) * 1000L);

      for (Player var3 : this.my.getPartyMembers()) {
         this.mA.add(var3.getRef());
         var3.addListener(this.mH);
         var3.addListener(this.mI);
         var3.addListener(this.mJ);
      }

      for (Player var7 : this.mz.getPartyMembers()) {
         this.mA.add(var7.getRef());
         var7.addListener(this.mH);
         var7.addListener(this.mI);
         var7.addListener(this.mJ);
      }

      this.startTime = System.currentTimeMillis() + this.mw * 1000L;

      for (Player var8 : HardReferences.unwrap(this.mA)) {
         this.mB.put(var8.getObjectId(), new MutableInt());
         var8.setCurrentCp(var8.getMaxCp());
         var8.setCurrentHp(var8.getMaxHp(), false);
         var8.setCurrentMp(var8.getMaxMp());
         var8.sendActionFailed();
      }

      this.active = true;
   }

   private void a(L2GameServerPacket var1) {
      for (Player var3 : HardReferences.unwrap(this.mA)) {
         var3.sendPacket(var1);
      }
   }

   private boolean isActive() {
      return this.active;
   }

   private boolean m(Player var1) {
      return this.mz.containsMember(var1);
   }

   private void end() {
      this.active = false;
      this.startCollapseTimer(60000L);
      this.paralyzePlayers();
      ThreadPoolManager.getInstance().schedule(new GvGInstance.Finish(), 55000L);
      if (this.mE != null) {
         this.mE.cancel(false);
         this.mE = null;
      }

      if (this.mF != null) {
         this.mF.cancel(false);
         this.mF = null;
      }

      if (this.mG != null) {
         this.mG.cancel(false);
         this.mG = null;
      }

      boolean var1 = false;
      var1 = this.aJ() >= this.aI();
      this.a(var1 ? this.mz : this.my);
      GvG.updateWinner(var1 ? this.mz.getPartyLeader() : this.my.getPartyLeader());
      this.mL.setActive(false);
      this.mN.setActive(false);
      this.mP.setActive(false);
   }

   private void a(Party var1) {
      for (Player var3 : var1.getPartyMembers()) {
         String var4 = new CustomMessage("scripts.event.gvg.youpartywin", var3).toString();
         var3.sendMessage(var4);
         Functions.addItem(var3, Config.GVG_REWARD_ID, Config.GVG_REWARD_AMOUNT);
      }
   }

   private synchronized void a(int var1, int var2, int var3, boolean var4, boolean var5, Player var6) {
      int var7 = (int)((this.startTime - System.currentTimeMillis()) / 1000L);
      if (var1 == 1) {
         if (var4) {
            this.mC -= var3;
            if (this.mC < 0) {
               this.mC = 0;
            }

            if (var5) {
               this.mD += var2;
            }
         } else {
            this.mC += var2;
            if (var5) {
               this.mD -= var3;
               if (this.mD < 0) {
                  this.mD = 0;
               }
            }
         }
      } else if (var1 == 2) {
         if (var4) {
            this.mD -= var3;
            if (this.mD < 0) {
               this.mD = 0;
            }

            if (var5) {
               this.mC += var2;
            }
         } else {
            this.mD += var2;
            if (var5) {
               this.mC -= var3;
               if (this.mC < 0) {
                  this.mC = 0;
               }
            }
         }
      }
   }

   private void n(Player var1) {
      MutableInt var2 = (MutableInt)this.mB.get(var1.getObjectId());
      var2.increment();
   }

   public int getPlayerScore(Player var1) {
      MutableInt var2 = (MutableInt)this.mB.get(var1.getObjectId());
      return var2.intValue();
   }

   public void paralyzePlayers() {
      for (Player var2 : HardReferences.unwrap(this.mA)) {
         if (var2.isDead()) {
            var2.setCurrentHp(var2.getMaxHp(), true);
            var2.broadcastPacket(new Revive(var2));
         } else {
            var2.setCurrentHp(var2.getMaxHp(), false);
         }

         var2.setCurrentMp(var2.getMaxMp());
         var2.setCurrentCp(var2.getMaxCp());
         var2.getEffectList().stopEffect(1411);
         var2.block();
      }
   }

   public void unParalyzePlayers() {
      for (Player var2 : HardReferences.unwrap(this.mA)) {
         var2.unblock();
         this.c(var2, true);
      }
   }

   private void cleanUp() {
      this.my = null;
      this.mz = null;
      this.mA.clear();
      this.mC = 0;
      this.mD = 0;
      this.mB.clear();
   }

   public void resurrectAtBase(Player var1) {
      if (var1.isDead()) {
         var1.setCurrentHp(0.7 * var1.getMaxHp(), true);
         var1.broadcastPacket(new Revive(var1));
      }

      Location var2;
      if (this.my.containsMember(var1)) {
         var2 = Location.findPointToStay(GvG.TEAM1_LOC, 0, 150, this.getGeoIndex());
      } else {
         var2 = Location.findPointToStay(GvG.TEAM2_LOC, 0, 150, this.getGeoIndex());
      }

      var1.teleToLocation(var2, this);
   }

   private void c(Player var1, boolean var2) {
      this.mA.remove(var1.getRef());
      var1.removeListener(this.mH);
      var1.removeListener(this.mI);
      var1.removeListener(this.mJ);
      var1.leaveParty();
      if (!var2) {
         var1.teleToLocation(Location.findPointToStay(GvG.RETURN_LOC, 0, 150, ReflectionManager.DEFAULT.getGeoIndex()), 0);
      }
   }

   private void b(Party var1) {
      if (var1 == this.my) {
         for (Player var3 : this.my.getPartyMembers()) {
            this.c(var3, false);
         }

         Player var4 = this.mz.getPartyLeader();
         this.a(2, 200, 0, false, false, var4);
      } else {
         for (Player var8 : this.mz.getPartyMembers()) {
            this.c(var8, false);
         }

         Player var6 = this.my.getPartyLeader();
         this.a(1, 200, 0, false, false, var6);
      }

      for (Player var9 : HardReferences.unwrap(this.mA)) {
         var9.sendPacket(
            new ExShowScreenMessage(new CustomMessage("scripts.event.gvg.partydispell", var9).toString(), 4000, ScreenMessageAlign.MIDDLE_CENTER, true)
         );
      }
   }

   private int aI() {
      return this.mC;
   }

   private int aJ() {
      return this.mD;
   }

   @Override
   public NpcInstance addSpawnWithoutRespawn(int var1, Location var2, int var3) {
      NpcInstance var4 = super.addSpawnWithoutRespawn(var1, var2, var3);
      var4.addListener(this.mH);
      return var4;
   }

   public class BattleEnd extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         for (Player var2 : HardReferences.unwrap(GvGInstance.this.mA)) {
            var2.sendPacket(
               new ExShowScreenMessage(new CustomMessage("scripts.event.gvg.teleport1min", var2).toString(), 4000, ScreenMessageAlign.MIDDLE_CENTER, true)
            );
         }

         GvGInstance.this.end();
      }
   }

   public class BossSpawn extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         for (Player var2 : HardReferences.unwrap(GvGInstance.this.mA)) {
            var2.sendPacket(
               new ExShowScreenMessage(new CustomMessage("scripts.event.gvg.geraldguard", var2).toString(), 5000, ScreenMessageAlign.MIDDLE_CENTER, true)
            );
         }

         GvGInstance.this.addSpawnWithoutRespawn(25655, new Location(79128, 91000, -2880, 4836), 0);
         GvGInstance.this.openDoor(22200004);
         GvGInstance.this.openDoor(22200005);
         GvGInstance.this.openDoor(22200006);
         GvGInstance.this.openDoor(22200007);
      }
   }

   public class CountingDown extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         for (Player var2 : HardReferences.unwrap(GvGInstance.this.mA)) {
            var2.sendPacket(
               new ExShowScreenMessage(new CustomMessage("scripts.event.gvg.1mintofinish", var2).toString(), 4000, ScreenMessageAlign.MIDDLE_CENTER, true)
            );
         }
      }
   }

   private class DeathListener implements OnDeathListener {
      private DeathListener() {
      }

      @Override
      public void onDeath(Creature var1, Creature var2) {
         if (GvGInstance.this.isActive()) {
            if (var1.getReflection() == var2.getReflection() && var1.getReflection() == GvGInstance.this) {
               if (var1.isPlayer() && var2.isPlayable()) {
                  if (GvGInstance.this.my.containsMember(var1.getPlayer()) && GvGInstance.this.mz.containsMember(var2.getPlayer())) {
                     GvGInstance.this.n(var2.getPlayer());
                     GvGInstance.this.a(1, 5, 3, true, true, var2.getPlayer());
                  } else if (GvGInstance.this.mz.containsMember(var1.getPlayer()) && GvGInstance.this.my.containsMember(var2.getPlayer())) {
                     GvGInstance.this.n(var2.getPlayer());
                     GvGInstance.this.a(2, 5, 3, true, true, var2.getPlayer());
                  }

                  GvGInstance.this.resurrectAtBase(var1.getPlayer());
               } else if (var1.isPlayer() && !var2.isPlayable()) {
                  GvGInstance.this.resurrectAtBase(var1.getPlayer());
               } else if (var1.isNpc() && var2.isPlayable()) {
                  if (var1.getNpcId() == 25656) {
                     if (GvGInstance.this.my.containsMember(var2.getPlayer())) {
                        GvGInstance.this.a(1, 20, 0, false, false, var2.getPlayer());
                     } else if (GvGInstance.this.mz.containsMember(var2.getPlayer())) {
                        GvGInstance.this.a(2, 20, 0, false, false, var2.getPlayer());
                     }
                  } else if (var1.getNpcId() == 25655) {
                     if (GvGInstance.this.my.containsMember(var2.getPlayer())) {
                        GvGInstance.this.a(1, 100, 0, false, false, var2.getPlayer());
                     } else if (GvGInstance.this.mz.containsMember(var2.getPlayer())) {
                        GvGInstance.this.a(2, 100, 0, false, false, var2.getPlayer());
                     }

                     GvGInstance.this.a(
                        new ExShowScreenMessage("Treasure guard Gerald died at the hand " + var2.getName(), 5000, ScreenMessageAlign.MIDDLE_CENTER, true)
                     );
                     GvGInstance.this.end();
                  }
               }
            }
         }
      }
   }

   public class Finish extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         GvGInstance.this.unParalyzePlayers();
         GvGInstance.this.cleanUp();
      }
   }

   private class PlayerPartyLeaveListener implements OnPlayerPartyLeaveListener {
      private PlayerPartyLeaveListener() {
      }

      @Override
      public void onPartyLeave(Player var1) {
         if (GvGInstance.this.isActive()) {
            Party var2 = var1.getParty();
            if (var2.getMemberCount() >= 3) {
               GvGInstance.this.c(var1, false);
            } else {
               GvGInstance.this.b(var2);
            }
         }
      }
   }

   private class TeleportListener implements OnTeleportListener {
      private TeleportListener() {
      }

      @Override
      public void onTeleport(Player var1, int var2, int var3, int var4, Reflection var5) {
         if (!GvGInstance.this.mL.checkIfInZone(var2, var3, var4, var5)
            && !GvGInstance.this.mN.checkIfInZone(var2, var3, var4, var5)
            && !GvGInstance.this.mP.checkIfInZone(var2, var3, var4, var5)) {
            GvGInstance.this.c(var1, false);
            var1.sendMessage(new CustomMessage("scripts.event.gvg.expelled", var1));
         }
      }
   }
}
