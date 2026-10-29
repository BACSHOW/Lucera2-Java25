/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectMutePhisycal
extends Effect {
    public EffectMutePhisycal(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        Skill skill;
        super.onStart();
        if (!this._effected.startPMuted() && (skill = this._effected.getCastingSkill()) != null && !skill.isMagic()) {
            this._effected.abortCast(true, true);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopPMuted();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

