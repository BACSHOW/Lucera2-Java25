/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class CPDam
extends Skill {
    public CPDam(StatsSet statsSet) {
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
            if (creature2 == null || creature2.isDead()) continue;
            creature2.doCounterAttack(this, creature, false);
            boolean bl3 = creature2.checkReflectSkill(creature, this);
            Creature creature3 = bl3 ? creature : creature2;
            if (creature3.isCurrentCpZero()) continue;
            double d = this._power * creature3.getCurrentCp();
            if (d < 1.0) {
                d = 1.0;
            }
            creature3.reduceCurrentHp(d, creature, this, true, true, false, true, false, false, true);
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false, bl3);
        }
    }
}

