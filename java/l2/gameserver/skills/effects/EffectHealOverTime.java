/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExRegenMax;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectHealOverTime
extends Effect {
    private final boolean aoJ;

    public EffectHealOverTime(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aoJ = effectTemplate.getParam().getBool("ignoreHpEff", false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.getEffected().isPlayer() && this.getCount() > 0 && this.getPeriod() > 0L) {
            this.getEffected().sendPacket((IStaticPacket)new ExRegenMax(this.calc(), (int)((long)this.getCount() * this.getPeriod() / 1000L), Math.round(this.getPeriod() / 1000L)));
        }
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isHealBlocked()) {
            return true;
        }
        double d = this.calc();
        double d2 = d * (!this.aoJ ? this._effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100.0, this._effector, this.getSkill()) : 100.0) / 100.0;
        double d3 = Math.max(0.0, Math.min(d2, this._effected.calcStat(Stats.HP_LIMIT, null, null) * (double)this._effected.getMaxHp() / 100.0 - this._effected.getCurrentHp()));
        if (d3 > 0.0) {
            this.getEffected().setCurrentHp(this._effected.getCurrentHp() + d3, false);
        }
        return true;
    }
}

