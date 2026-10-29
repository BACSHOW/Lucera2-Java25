/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectMuteAll
extends Effect {
    public EffectMuteAll(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startMuted();
        this._effected.startPMuted();
        this._effected.abortCast(true, true);
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopMuted();
        this._effected.stopPMuted();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

