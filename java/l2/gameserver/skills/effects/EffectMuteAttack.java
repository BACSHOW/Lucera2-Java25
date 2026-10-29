/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectMuteAttack
extends Effect {
    public EffectMuteAttack(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!this._effected.startAMuted()) {
            this._effected.abortCast(true, true);
            this._effected.abortAttack(true, true);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopAMuted();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

