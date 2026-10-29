/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectDamOverTimeLethal
extends Effect {
    public EffectDamOverTimeLethal(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isDead()) {
            return false;
        }
        double d = this.calc();
        if (this.getSkill().isOffensive()) {
            d *= 2.0;
        }
        d = this._effector.calcStat(this.getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, d, this._effected, this.getSkill());
        this._effected.reduceCurrentHp(d, this._effector, this.getSkill(), !this._effected.isNpc() && this._effected != this._effector, this._effected != this._effector, this._effector.isNpc() || this._effected == this._effector, false, false, true, false);
        return true;
    }
}

