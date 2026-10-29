package ai;

import java.util.ArrayList;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.Fighter;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.ReflectionUtils;

public class Archangel extends Fighter {
   private static final int BAIUM_NPC_ID = 29020;
   private final Zone _zone = ReflectionUtils.getZone("[baium_epic]");
   private long aY;

   public Archangel(NpcInstance var1) {
      super(var1);
   }

   @Override
   public boolean isGlobalAI() {
      return true;
   }

   @Override
   protected boolean thinkActive() {
      NpcInstance var1 = null;

      for (Creature var5 : this._zone.getObjects()) {
         if (var5 != null && var5.isNpc() && var5.getNpcId() == 29020) {
            var1 = (NpcInstance)var5;
            break;
         }
      }

      return var1 == null ? false : super.thinkActive();
   }

   @Override
   protected void thinkAttack() {
      NpcInstance var1 = this.getActor();
      if (var1 != null) {
         if (this.aY < System.currentTimeMillis()) {
            ArrayList var2 = new ArrayList();

            for (Creature var6 : this._zone.getObjects()) {
               if (var6 != null && !var6.isDead()) {
                  if (var6.getNpcId() == 29020) {
                     if (Rnd.chance(10)) {
                        var2.add(var6);
                     }
                  } else {
                     var2.add(var6);
                  }
               }
            }

            if (!var2.isEmpty()) {
               Creature var7 = (Creature)var2.get(Rnd.get(var2.size()));
               if (var7 != null && (var7.getNpcId() == 29020 || var7.isPlayer())) {
                  this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, var7);
                  var1.getAggroList().addDamageHate(var7, 100, 10);
               }
            }

            this.aY = System.currentTimeMillis() + 20000L;
         }

         super.thinkAttack();
      }
   }

   @Override
   protected void onEvtAttacked(Creature var1, int var2) {
      NpcInstance var3 = this.getActor();
      if (var3 != null && !var3.isDead() && var1 != null && var1.getNpcId() == 29020) {
         var3.getAggroList().addDamageHate(var1, var2, 10);
         this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, var1);
      }

      super.onEvtAttacked(var1, var2);
   }

   @Override
   protected boolean maybeMoveToHome() {
      NpcInstance var1 = this.getActor();
      if (var1 != null && !var1.isDead() && !this._zone.checkIfInZone(var1)) {
         this.returnHome();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void returnHome() {
      NpcInstance var1 = this.getActor();
      Location var2 = var1.getSpawnedLoc();
      this.clearTasks();
      var1.stopMove();
      var1.getAggroList().clear(true);
      this.setAttackTimeout(Long.MAX_VALUE);
      this.setAttackTarget(null);
      this.changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
      var1.broadcastPacketToOthers(new MagicSkillUse(var1, var1, 2036, 1, 500, 0L));
      var1.teleToLocation(var2.x, var2.y, GeoEngine.getHeight(var2, var1.getGeoIndex()));
   }
}
