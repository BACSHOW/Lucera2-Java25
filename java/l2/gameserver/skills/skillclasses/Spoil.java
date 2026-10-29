package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

public class Spoil extends Skill {
   public Spoil(StatsSet var1) {
      super(var1);
   }

   @Override
   public void useSkill(Creature var1, List<Creature> var2) {
      if (var1.isPlayer()) {
         int var3 = this.isSSPossible() ? (this.isMagic() ? var1.getChargedSpiritShot() : (var1.getChargedSoulShot() ? 2 : 0)) : 0;
         if (var3 > 0 && this.getPower() > 0.0) {
            var1.unChargeShots(false);
         }

         for (Creature var5 : var2) {
            if (var5 != null && !var5.isDead()) {
               if (var5.isMonster()) {
                  if (((MonsterInstance)var5).isSpoiled()) {
                     var1.sendPacket(Msg.ALREADY_SPOILED);
                  } else {
                     MonsterInstance var6 = (MonsterInstance)var5;
                     int var8 = var6.getLevel();
                     int var9 = var8 - this.getMagicLevel();
                     double var10 = Math.max(this.getActivateRate(), 80);
                     if (var9 > 8) {
                        var10 -= var10 * (var9 - 8) * 9.0 / 100.0;
                     }

                     var10 *= (double)this.getMagicLevel() / var8;
                     var10 = Math.max(Config.MINIMUM_SPOIL_RATE, Math.min(var10, 99.0));
                     boolean var7 = Rnd.chance(var10);
                     if (var7 && var6.setSpoiled((Player)var1)) {
                        var1.sendPacket(Msg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
                     }
                  }
               }

               if (this.getPower() > 0.0) {
                  double var12;
                  if (this.isMagic()) {
                     var12 = Formulas.calcMagicDam(var1, var5, this, var3);
                  } else {
                     Formulas.AttackInfo var13 = Formulas.calcPhysDam(var1, var5, this, false, false, var3 > 0, false);
                     var12 = var13.damage;
                     if (var13.lethal_dmg > 0.0) {
                        var5.reduceCurrentHp(var13.lethal_dmg, var1, this, true, true, false, false, false, false, false);
                     }
                  }

                  var5.reduceCurrentHp(var12, var1, this, true, true, false, true, false, false, true);
                  var5.doCounterAttack(this, var1, false);
               }

               this.getEffects(var1, var5, false, false);
               var5.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, var1, Math.max(this._effectPoint, 1));
            }
         }
      }
   }
}
