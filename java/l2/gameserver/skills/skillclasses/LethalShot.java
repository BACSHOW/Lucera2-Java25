/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.Formulas;
import l2.gameserver.templates.StatsSet;

public class LethalShot
extends Skill {
    public LethalShot(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        boolean bl;
        boolean bl2 = bl = creature.getChargedSoulShot() && this.isSSPossible();
        if (bl) {
            creature.unChargeShots(false);
        }
        for (Creature creature2 : list) {
            Creature creature3;
            if (creature2 == null || creature2.isDead()) continue;
            boolean bl3 = creature2.checkReflectSkill(creature, this);
            Creature creature4 = creature3 = bl3 ? creature : creature2;
            if (this.getPower() > 0.0) {
                Formulas.AttackInfo attackInfo = Formulas.calcPhysDam(creature, creature3, this, false, false, bl, false);
                if (attackInfo.lethal_dmg > 0.0) {
                    creature3.reduceCurrentHp(attackInfo.lethal_dmg, creature, this, true, true, false, false, false, false, false);
                }
                creature3.reduceCurrentHp(attackInfo.damage, creature, this, true, true, false, true, false, false, true);
                if (!bl3) {
                    creature3.doCounterAttack(this, creature, false);
                }
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false, bl3);
        }
    }
}

