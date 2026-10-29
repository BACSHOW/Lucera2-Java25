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

public class EffectHealCPPercent
extends Effect {
    private final boolean aoK;

    public EffectHealCPPercent(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aoK = effectTemplate.getParam().getBool("ignoreCpEff", true);
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
        double d = this.calc() * (double)this._effected.getMaxCp() / 100.0;
        double d2 = d * (!this.aoK ? this._effected.calcStat(Stats.CPHEAL_EFFECTIVNESS, 100.0, this._effector, this.getSkill()) : 100.0) / 100.0;
        double d3 = Math.max(0.0, Math.min(d2, this._effected.calcStat(Stats.CP_LIMIT, null, null) * (double)this._effected.getMaxCp() / 100.0 - this._effected.getCurrentCp()));
        if (this._effected == this._effector) {
            this._effected.sendPacket((IStaticPacket)new SystemMessage(1405).addNumber((long)d3));
        } else {
            this._effected.sendPacket((IStaticPacket)new SystemMessage(1406).addName(this._effector).addNumber((long)d3));
        }
        if (d3 > 0.0) {
            this._effected.setCurrentCp(d3 + this._effected.getCurrentCp());
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

