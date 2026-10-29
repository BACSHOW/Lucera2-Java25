/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.commons.util.Rnd;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectStun
extends Effect {
    public EffectStun(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        return Rnd.chance(this._template.chance(100));
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.startStunning();
        this._effected.abortAttack(true, true);
        this._effected.abortCast(true, true);
        this._effected.stopMove();
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopStunning();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

