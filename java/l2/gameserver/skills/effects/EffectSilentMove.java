/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Playable;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectSilentMove
extends Effect {
    public EffectSilentMove(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this._effected.isPlayable()) {
            ((Playable)this._effected).startSilentMoving();
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (this._effected.isPlayable()) {
            ((Playable)this._effected).stopSilentMoving();
        }
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isDead()) {
            return false;
        }
        if (!this.getSkill().isToggle()) {
            return false;
        }
        double d = this.calc();
        if (d > this._effected.getCurrentMp()) {
            this._effected.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
            this._effected.sendPacket((IStaticPacket)new SystemMessage(749).addSkillName(this.getSkill().getId(), this.getSkill().getDisplayLevel()));
            return false;
        }
        this._effected.reduceCurrentMp(d, null);
        return true;
    }
}

