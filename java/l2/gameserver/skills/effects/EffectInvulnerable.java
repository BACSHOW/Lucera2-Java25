/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectInvulnerable
extends Effect {
    public EffectInvulnerable(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (this._effected.isInvul()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startHealBlocked();
        this._effected.setIsInvul(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopHealBlocked();
        this._effected.setIsInvul(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

