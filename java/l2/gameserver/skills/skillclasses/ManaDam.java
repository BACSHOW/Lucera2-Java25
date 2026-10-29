package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class ManaDam extends Skill {
   public ManaDam(StatsSet var1) {
      super(var1);
   }

   @Override
   public void useSkill(Creature var1, List<Creature> var2) {
      int var3 = 0;
      if (this.isSSPossible()) {
         var3 = var1.getChargedSpiritShot();
      }

      for (Creature var5 : var2) {
         if (var5 != null && !var5.isDead()) {
            double var6 = var1.getMAtk(var5, this);
            if (var3 == 2) {
               var6 *= 4.0;
            } else if (var3 == 1) {
               var6 *= 2.0;
            }

            double var8 = var5.getMDef(var1, this);
            double var10 = var5.getMaxMp();
            double var12 = Math.sqrt(var6) * this.getPower() * var10 / 108.0 / var8;
            if (Config.MDAM_CRIT_POSSIBLE) {
               boolean var14 = Formulas.calcMCrit(var1, var5, var1.getMagicCriticalRate(var5, this));
               if (var14) {
                  var1.sendPacket(Msg.MAGIC_CRITICAL_HIT);
                  var12 *= var1.calcStat(Stats.MCRITICAL_DAMAGE, 4.0, var5, this);
               }
            }

            if (var1.isPlayer() && var5.isPlayer() && var12 > 1.0) {
               var12 += var12
                  * (var1.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0, null, null) - var5.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1.0, null, null));
            }

            int var21 = var5.getLevel() - var1.getLevel();
            double var15 = var5.calcStat(Stats.MAGIC_RESIST, var1, this) - var1.calcStat(Stats.MAGIC_POWER, var5, this);
            double var17 = 4.0 * Math.max(1.0, var21) * (1.0 + var15 / 100.0);
            if (Rnd.chance(var17)) {
               if (var21 > 9) {
                  var12 = 0.0;
                  SystemMessage var19 = new SystemMessage(158);
                  SystemMessage var20 = new SystemMessage(159).addName(var5);
                  var1.sendPacket(var19);
                  var5.sendPacket(var20);
               } else {
                  var12 /= 2.0;
                  SystemMessage var22 = new SystemMessage(158);
                  SystemMessage var23 = new SystemMessage(159).addName(var5);
                  var1.sendPacket(var22);
                  var5.sendPacket(var23);
               }
            }

            var5.reduceCurrentMp(var12, var1, var12 > 0.0);
            this.getEffects(var1, var5, this.getActivateRate() > 0, false);
         }
      }

      if (this.isSSPossible()) {
         var1.unChargeShots(this.isMagic());
      }
   }
}
