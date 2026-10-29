/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.effects.EffectSkillSeed;
import l2.gameserver.templates.StatsSet;

public class SkillSeed
extends Skill {
    public SkillSeed(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (creature.isAlikeDead()) {
            return;
        }
        for (Creature creature2 : list) {
            if (creature2.isAlikeDead() && this.getTargetType() != Skill.SkillTargetType.TARGET_CORPSE) continue;
            List<Effect> list2 = creature2.getEffectList().getEffectsBySkill(this);
            boolean bl = false;
            if (list2 != null && !list2.isEmpty()) {
                for (Effect effect : list2) {
                    if (!(effect instanceof EffectSkillSeed)) continue;
                    EffectSkillSeed effectSkillSeed = (EffectSkillSeed)effect;
                    effectSkillSeed.incSeeds();
                    bl = true;
                }
            }
            if (bl) continue;
            this.getEffects(creature, creature2, false, false);
        }
    }
}

