/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectHealPercent
extends Effect {
    public EffectHealPercent(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (this._effected.isHealBlocked()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this._effected.isHealBlocked()) {
            return;
        }
        double d = this.calc() * (double)this._effected.getMaxHp() / 100.0;
        double d2 = Math.max(0.0, Math.min(d, this._effected.calcStat(Stats.HP_LIMIT, null, null) * (double)this._effected.getMaxHp() / 100.0 - this._effected.getCurrentHp()));
        this._effected.sendPacket((IStaticPacket)new SystemMessage(1066).addNumber(Math.round(d2)));
        if (d2 > 0.0) {
            this._effected.setCurrentHp(d2 + this._effected.getCurrentHp(), false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

