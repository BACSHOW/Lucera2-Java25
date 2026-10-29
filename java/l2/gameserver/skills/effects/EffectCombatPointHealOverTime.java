/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectCombatPointHealOverTime
extends Effect {
    public EffectCombatPointHealOverTime(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isHealBlocked()) {
            return true;
        }
        double d = Math.max(0.0, Math.min(this.calc(), this._effected.calcStat(Stats.CP_LIMIT, null, null) * (double)this._effected.getMaxCp() / 100.0 - this._effected.getCurrentCp()));
        if (d > 0.0) {
            this._effected.setCurrentCp(this._effected.getCurrentCp() + d);
        }
        return true;
    }
}

