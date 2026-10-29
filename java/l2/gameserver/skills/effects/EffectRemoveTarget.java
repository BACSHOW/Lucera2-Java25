/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectRemoveTarget
extends Effect {
    private boolean aoN;

    public EffectRemoveTarget(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aoN = effectTemplate.getParam().getBool("doStopTarget", false);
    }

    @Override
    public boolean checkCondition() {
        return Rnd.chance(this._template.chance(100));
    }

    @Override
    public void onStart() {
        if (this.getEffected().getAI() instanceof DefaultAI) {
            ((DefaultAI)this.getEffected().getAI()).setGlobalAggro(System.currentTimeMillis() + 3000L);
        }
        this.getEffected().setTarget(null);
        if (this.aoN) {
            this.getEffected().stopMove();
        }
        this.getEffected().abortAttack(true, true);
        this.getEffected().abortCast(true, true);
        this.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, this.getEffector());
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

