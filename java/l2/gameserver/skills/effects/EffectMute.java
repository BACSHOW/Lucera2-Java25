/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Skill;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectMute
extends Effect {
    public EffectMute(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        Skill skill;
        super.onStart();
        if (!this._effected.startMuted() && (skill = this._effected.getCastingSkill()) != null && skill.isMagic()) {
            this._effected.abortCast(true, true);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopMuted();
    }
}

