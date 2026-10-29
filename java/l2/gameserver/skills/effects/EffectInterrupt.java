/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectInterrupt
extends Effect {
    public EffectInterrupt(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!this.getEffected().isRaid()) {
            this.getEffected().abortCast(false, true);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

