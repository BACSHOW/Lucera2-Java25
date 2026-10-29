/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectImmobilize
extends Effect {
    public EffectImmobilize(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startImmobilized();
        this._effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopImmobilized();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

