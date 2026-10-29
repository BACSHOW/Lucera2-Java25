/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectDebuffImmunity
extends Effect {
    public EffectDebuffImmunity(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getEffected().startDebuffImmunity();
    }

    @Override
    public void onExit() {
        super.onExit();
        this.getEffected().stopDebuffImmunity();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

