/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class Toggle
extends Skill {
    public Toggle(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (creature.getEffectList().getEffectsBySkillId(this._id) != null) {
            creature.getEffectList().stopEffect(this._id);
            creature.sendActionFailed();
            return;
        }
        this.getEffects(creature, creature, this.getActivateRate() > 0, false);
    }
}

