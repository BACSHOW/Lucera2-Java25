/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectManaDamOverTime
extends Effect {
    public EffectManaDamOverTime(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isDead() && !this._skill.isPreservedOnDeath()) {
            return false;
        }
        double d = this.calc();
        if (d > this._effected.getCurrentMp() && this.getSkill().isToggle()) {
            this._effected.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
            this._effected.sendPacket((IStaticPacket)new SystemMessage(749).addSkillName(this.getSkill().getId(), this.getSkill().getDisplayLevel()));
            return false;
        }
        this._effected.reduceCurrentMp(d, null);
        return true;
    }
}

