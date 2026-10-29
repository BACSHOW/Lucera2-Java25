/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.templates.StatsSet;

public class EffectsFromSkills
extends Skill {
    public EffectsFromSkills(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            if (creature2 == null) continue;
            for (Skill.AddedSkill addedSkill : this.getAddedSkills()) {
                addedSkill.getSkill().getEffects(creature, creature2, false, false);
            }
        }
    }
}

