/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectAggression
extends Effect {
    public EffectAggression(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this._effected.isPlayer() && this._effected != this._effector) {
            this._effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this._effector);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

