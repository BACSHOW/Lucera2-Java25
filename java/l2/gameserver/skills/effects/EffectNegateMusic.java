/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectNegateMusic
extends Effect {
    public EffectNegateMusic(Env env, EffectTemplate effectTemplate) {
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
            if (!effect.getSkill().isMusic()) continue;
            effect.exit();
        }
        return false;
    }
}

