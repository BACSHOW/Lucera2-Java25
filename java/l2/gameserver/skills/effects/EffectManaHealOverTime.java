/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectManaHealOverTime
extends Effect {
    private final boolean aoL;

    public EffectManaHealOverTime(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aoL = effectTemplate.getParam().getBool("ignoreMpEff", false);
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isHealBlocked()) {
            return true;
        }
        double d = this.calc();
        double d2 = d * (!this.aoL ? this._effected.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0, this._effector, this.getSkill()) : 100.0) / 100.0;
        double d3 = Math.max(0.0, Math.min(d2, this._effected.calcStat(Stats.MP_LIMIT, null, null) * (double)this._effected.getMaxMp() / 100.0 - this._effected.getCurrentMp()));
        if (d3 > 0.0) {
            this._effected.setCurrentMp(this._effected.getCurrentMp() + d3);
        }
        return true;
    }
}

