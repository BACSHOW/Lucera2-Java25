package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

public class MDam extends Skill {
   public MDam(StatsSet var1) {
      super(var1);
   }

   @Override
   public boolean checkCondition(Creature var1, Creature var2, boolean var3, boolean var4, boolean var5) {
      if (this._targetType != Skill.SkillTargetType.TARGET_AREA_AIM_CORPSE || var2 != null && var2.isDead() && (var2.isNpc() || var2.isSummon())) {
         return super.checkCondition(var1, var2, var3, var4, var5);
      }

      var1.sendPacket(SystemMsg.INVALID_TARGET);
      return false;
   }

   @Override
   public void useSkill(Creature var1, List<Creature> var2) {
      int var3 = this.isSSPossible() ? (this.isMagic() ? var1.getChargedSpiritShot() : (var1.getChargedSoulShot() ? 2 : 0)) : 0;

      for (Creature var7 : var2) {
         if (var7 != null && !var7.isDead()) {
            boolean var5 = var7.checkReflectSkill(var1, this);
            Creature var4 = var5 ? var1 : var7;
            double var8 = Formulas.calcMagicDam(var1, var4, this, var3);
            if (var8 >= 1.0) {
               var4.reduceCurrentHp(var8, var1, this, true, true, false, true, false, false, true);
            }

            this.getEffects(var1, var7, this.getActivateRate() > 0, false, var5);
         }
      }

      if (this.isSuicideAttack()) {
         var1.doDie(null);
      } else if (this.isSSPossible()) {
         var1.unChargeShots(this.isMagic());
      }

      if (this._targetType == Skill.SkillTargetType.TARGET_AREA_AIM_CORPSE && var2.size() > 0) {
         Creature var10 = (Creature)var2.get(0);
         if (var10 != null && var10.isDead()) {
            if (var10.isNpc()) {
               ((NpcInstance)var10).endDecayTask();
            } else if (var10.isSummon()) {
               ((SummonInstance)var10).endDecayTask();
            }

            var1.getAI().setAttackTarget(null);
         }
      }
   }
}
