/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectAddSkills
extends Effect {
    public EffectAddSkills(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        for (Skill.AddedSkill addedSkill : this.getSkill().getAddedSkills()) {
            this.getEffected().addSkill(addedSkill.getSkill());
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        for (Skill.AddedSkill addedSkill : this.getSkill().getAddedSkills()) {
            this.getEffected().removeSkill(addedSkill.getSkill());
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

