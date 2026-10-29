/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectHealBlock
extends Effect {
    public EffectHealBlock(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (this._effected.isHealBlocked()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startHealBlocked();
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopHealBlocked();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

