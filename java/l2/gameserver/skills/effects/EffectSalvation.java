/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectSalvation
extends Effect {
    private final int aoO;

    public EffectSalvation(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aoO = effectTemplate.getParam().getInteger("expireResurrectTime", 0);
    }

    @Override
    public boolean checkCondition() {
        return this.getEffected().isPlayer() && super.checkCondition();
    }

    @Override
    public void onStart() {
        this.getEffected().setSalvationWindowTime(this.aoO);
    }

    @Override
    public void onExit() {
        super.onExit();
        this.getEffected().setSalvationWindowTime(-1);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

