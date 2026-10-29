/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectSleep
extends Effect {
    public EffectSleep(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startSleeping();
        this._effected.abortAttack(true, true);
        this._effected.abortCast(true, true);
        this._effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopSleeping();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

