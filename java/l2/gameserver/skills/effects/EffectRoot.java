/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectRoot
extends Effect {
    public EffectRoot(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startRooted();
        this._effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopRooted();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

