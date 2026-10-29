/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.network.l2.s2c.FinishRotating;
import l2.gameserver.network.l2.s2c.StartRotating;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectBluff
extends Effect {
    public EffectBluff(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (this.getEffected().isNpc() && !this.getEffected().isMonster()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        this.getEffected().broadcastPacket(new StartRotating(this.getEffected(), this.getEffected().getHeading(), 1, 65535));
        this.getEffected().broadcastPacket(new FinishRotating(this.getEffected(), this.getEffector().getHeading(), 65535));
        this.getEffected().setHeading(this.getEffector().getHeading());
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

