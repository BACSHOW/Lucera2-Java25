/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.StatsSet;

public class Drain
extends Skill {
    private double ape;

    public Drain(StatsSet statsSet) {
        super(statsSet);
        this.ape = statsSet.getDouble("absorbAbs", 0.0);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        int n = this.isSSPossible() ? creature.getChargedSpiritShot() : 0;
        boolean bl = this.isSSPossible() && creature.getChargedSoulShot();
        boolean bl2 = this._targetType == Skill.SkillTargetType.TARGET_CORPSE;
        for (Creature creature2 : list) {
            Creature creature3;
            if (creature2 == null) continue;
            boolean bl3 = !bl2 && creature2.checkReflectSkill(creature, this);
            Creature creature4 = creature3 = bl3 ? creature : creature2;
            if (this.getPower() > 0.0 || this.ape > 0.0) {
                double d;
                if (creature3.isDead() && !bl2) continue;
                double d2 = 0.0;
                double d3 = creature3.getCurrentHp();
                if (!bl2) {
                    if (this.isMagic()) {
                        d = Formulas.calcMagicDam(creature, creature3, this, n);
                    } else {
                        Formulas.AttackInfo attackInfo = Formulas.calcPhysDam(creature, creature3, this, false, false, bl, false);
                        d = attackInfo.damage;
                        if (attackInfo.lethal_dmg > 0.0) {
                            creature3.reduceCurrentHp(attackInfo.lethal_dmg, creature, this, true, true, false, false, false, false, false);
                        }
                    }
                    double d4 = creature3.getCurrentCp();
                    if (d > d4 || !creature3.isPlayer()) {
                        d2 = (d - d4) * this._absorbPart;
                    }
                    creature3.reduceCurrentHp(d, creature, this, true, true, false, true, false, false, true);
                    if (!bl3) {
                        creature3.doCounterAttack(this, creature, false);
                    }
                }
                if (this.ape == 0.0 && this._absorbPart == 0.0) continue;
                if ((d2 += this.ape) > d3 && !bl2) {
                    d2 = d3;
                }
                if ((d = Math.max(0.0, Math.min(d2, creature.calcStat(Stats.HP_LIMIT, null, null) * (double)creature.getMaxHp() / 100.0 - creature.getCurrentHp()))) > 0.0 && !creature.isHealBlocked()) {
                    creature.setCurrentHp(creature.getCurrentHp() + d, false);
                }
                if (creature3.isDead() && bl2 && creature3.isNpc()) {
                    creature.getAI().setAttackTarget(null);
                    ((NpcInstance)creature3).endDecayTask();
                }
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false, bl3);
        }
        if (this.isMagic() ? n != 0 : bl) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

