/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class Disablers
extends Skill {
    private final boolean _skillInterrupt;

    public Disablers(StatsSet statsSet) {
        super(statsSet);
        this._skillInterrupt = statsSet.getBool("skillInterrupt", false);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            Creature creature3;
            if (creature2 == null) continue;
            boolean bl = creature2.checkReflectSkill(creature, this);
            Creature creature4 = creature3 = bl ? creature : creature2;
            if (this._skillInterrupt) {
                if (creature3.getCastingSkill() != null && !creature3.getCastingSkill().isMagic() && !creature3.isRaid()) {
                    creature3.abortCast(false, true);
                }
                if (!creature3.isRaid()) {
                    creature3.abortAttack(true, true);
                }
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false, bl);
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }
}

