/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectNegateEffects
extends Effect {
    public EffectNegateEffects(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onExit() {
        super.onExit();
    }

    @Override
    public boolean onActionTime() {
        for (Effect effect : this._effected.getEffectList().getAllEffects()) {
            if ((effect.getStackType().equals("none") || !effect.getStackType().equals(this.getStackType()) && !effect.getStackType().equals(this.getStackType2())) && (effect.getStackType2().equals("none") || !effect.getStackType2().equals(this.getStackType()) && !effect.getStackType2().equals(this.getStackType2())) || effect.getStackOrder() > this.getStackOrder()) continue;
            effect.exit();
        }
        return false;
    }
}

