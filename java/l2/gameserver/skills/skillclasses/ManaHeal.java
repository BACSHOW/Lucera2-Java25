package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class ManaHeal extends Skill {
   private final boolean aoL;

   public ManaHeal(StatsSet var1) {
      super(var1);
      this.aoL = var1.getBool("ignoreMpEff", false);
   }

   @Override
   public void useSkill(Creature var1, List<Creature> var2) {
      double var3 = this._power;
      int var5 = this.isSSPossible() ? var1.getChargedSpiritShot() : 0;
      if (var5 > 0 && Config.MANAHEAL_SPS_BONUS) {
         var3 *= var5 == 2 ? 1.5 : 1.3;
      }

      for (Creature var7 : var2) {
         if (!var7.isHealBlocked()) {
            double var8 = var1 == var7
               ? var3
               : Math.min(var3 * 1.7, var3 * (!this.aoL ? var7.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0, var1, this) : 100.0) / 100.0);
            if (this.getMagicLevel() > 0 && var1 != var7) {
               int var10 = var7.getLevel() - this.getMagicLevel();
               if (var10 > 5) {
                  if (var10 < 20) {
                     var8 = var8 / 100.0 * (100 - var10 * 5);
                  } else {
                     var8 = 0.0;
                  }
               }
            }

            if (var8 == 0.0) {
               var1.sendPacket(new SystemMessage(1597).addSkillName(this._id, this.getDisplayLevel()));
               this.getEffects(var1, var7, this.getActivateRate() > 0, false);
            } else {
               double var12 = Math.max(0.0, Math.min(var8, var7.calcStat(Stats.MP_LIMIT, null, null) * var7.getMaxMp() / 100.0 - var7.getCurrentMp()));
               if (var12 > 0.0) {
                  var7.setCurrentMp(var12 + var7.getCurrentMp());
               }

               if (var7.isPlayer()) {
                  if (var1 != var7) {
                     var7.sendPacket(new SystemMessage(1069).addString(var1.getName()).addNumber(Math.round(var12)));
                  } else {
                     var1.sendPacket(new SystemMessage(1068).addNumber(Math.round(var12)));
                  }
               }

               this.getEffects(var1, var7, this.getActivateRate() > 0, false);
            }
         }
      }

      if (this.isSSPossible()) {
         var1.unChargeShots(this.isMagic());
      }
   }
}
