/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectPetrification
extends Effect {
    public EffectPetrification(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (this._effected.isParalyzeImmune()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startParalyzed();
        this._effected.startDebuffImmunity();
        this._effected.startBuffImmunity();
        this._effected.startDamageBlocked();
        this._effected.abortAttack(true, true);
        this._effected.abortCast(true, true);
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopParalyzed();
        this._effected.stopDebuffImmunity();
        this._effected.stopBuffImmunity();
        this._effected.stopDamageBlocked();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

