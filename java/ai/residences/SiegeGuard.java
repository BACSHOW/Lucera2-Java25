package ai.residences;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public abstract class SiegeGuard extends DefaultAI {
   public SiegeGuard(NpcInstance var1) {
      super(var1);
      this.MAX_PURSUE_RANGE = 1000;
   }

   @Override
   public int getMaxPathfindFails() {
      return Integer.MAX_VALUE;
   }

   @Override
   public int getMaxAttackTimeout() {
      return 0;
   }

   @Override
   protected boolean randomWalk() {
      return false;
   }

   @Override
   protected boolean randomAnimation() {
      return false;
   }

   @Override
   public boolean canSeeInSilentMove(Playable var1) {
      return !var1.isSilentMoving() || Rnd.chance(10);
   }

   @Override
   protected boolean isAggressive() {
      return true;
   }

   @Override
   protected boolean isGlobalAggro() {
      return true;
   }

   @Override
   protected void onEvtAggression(Creature var1, int var2) {
      NpcInstance var3 = this.getActor();
      if (!var3.isDead()) {
         if (var1 != null && var3.isAutoAttackable(var1)) {
            super.onEvtAggression(var1, var2);
         }
      }
   }

   @Override
   protected boolean thinkActive() {
      NpcInstance var1 = this.getActor();
      if (var1.isActionsDisabled()) {
         return true;
      }

      if (this._def_think) {
         if (this.doTask()) {
            this.clearTasks();
         }

         return true;
      } else {
         long var2 = System.currentTimeMillis();
         if (var2 - this._checkAggroTimestamp > Config.AGGRO_CHECK_INTERVAL) {
            this._checkAggroTimestamp = var2;
            List var4 = World.getAroundCharacters(var1);

            while (!var4.isEmpty()) {
               Creature var5 = this.getNearestTarget(var4);
               if (var5 == null) {
                  break;
               }

               if (this.checkAggression(var5)) {
                  var1.getAggroList().addDamageHate(var5, 0, 2);
                  if (var5.isSummon()) {
                     var1.getAggroList().addDamageHate(var5.getPlayer(), 0, 1);
                  }

                  this.startRunningTask(this.AI_TASK_ATTACK_DELAY);
                  this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, var5);
                  return true;
               }

               var4.remove(var5);
            }
         }

         Location var6 = var1.getSpawnedLoc();
         if (!var1.isInRange(var6, 250L)) {
            this.teleportHome();
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   protected Creature prepareTarget() {
      NpcInstance var1 = this.getActor();
      if (var1.isDead()) {
         return null;
      }

      List var2 = var1.getAggroList().getHateList(this.MAX_PURSUE_RANGE);
      Creature var3 = null;

      for (Object __raw_var5_125 : (Iterable<?>)(Object)(var2)) {
         Creature var5 = (Creature)__raw_var5_125;
         if (this.checkTarget(var5, this.MAX_PURSUE_RANGE)) {
            var3 = var5;
            break;
         }

         var1.getAggroList().remove(var5, true);
      }

      if (var3 != null) {
         this.setAttackTarget(var3);
         return var3;
      } else {
         return null;
      }
   }

   @Override
   protected boolean canAttackCharacter(Creature var1) {
      return this.getActor().isAutoAttackable(var1);
   }
}
