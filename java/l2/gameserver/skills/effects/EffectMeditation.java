/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectMeditation
extends Effect {
    public EffectMeditation(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startParalyzed();
        this._effected.setMeditated(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopParalyzed();
        this._effected.setMeditated(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

