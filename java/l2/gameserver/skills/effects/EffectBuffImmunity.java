/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectBuffImmunity
extends Effect {
    private final boolean aoq;

    public EffectBuffImmunity(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aoq = effectTemplate.getParam().getBool("ignoreSelfBuff", false);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getEffected().startBuffImmunity();
    }

    @Override
    public void onExit() {
        this.getEffected().stopBuffImmunity();
        super.onExit();
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isDead()) {
            return false;
        }
        return this.getSkill().isToggle();
    }

    public boolean isIgnoreSelfBuff() {
        return this.aoq;
    }
}

