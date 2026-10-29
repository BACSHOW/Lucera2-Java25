/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;

public class EffectDamOverTime
extends Effect {
    private static int[] aow = new int[]{12, 17, 25, 34, 44, 54, 62, 67, 72, 77, 82, 87};
    private static int[] aox = new int[]{11, 16, 24, 32, 41, 50, 58, 63, 68, 72, 77, 82};
    private boolean aoy = this.getTemplate().getParam().getBool("percent", false);

    public EffectDamOverTime(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isDead()) {
            return false;
        }
        double d = this.calc();
        if (this.aoy) {
            d = (double)this._effected.getMaxHp() * this._template._value * 0.01;
        }
        if (d < 2.0 && this.getStackOrder() != -1) {
            switch (this.getEffectType()) {
                case Poison: {
                    d = (long)aox[this.getStackOrder() - 1] * this.getPeriod() / 1000L;
                    break;
                }
                case Bleed: {
                    d = (long)aow[this.getStackOrder() - 1] * this.getPeriod() / 1000L;
                }
            }
        }
        if ((d = this._effector.calcStat(this.getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, d, this._effected, this.getSkill())) > this._effected.getCurrentHp() - 1.0) {
            if (!this.getSkill().isOffensive()) {
                this._effected.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_HP);
            }
            return this.getEffectType() == EffectType.Bleed || this.getEffectType() == EffectType.Poison;
        }
        if (this.getSkill().getAbsorbPart() > 0.0) {
            this._effector.setCurrentHp(this.getSkill().getAbsorbPart() * Math.min(this._effected.getCurrentHp(), d) + this._effector.getCurrentHp(), false);
        }
        this._effected.reduceCurrentHp(d, this._effector, this.getSkill(), !this._effected.isNpc() && this._effected != this._effector, this._effected != this._effector, this._effector.isNpc() || this._effected == this._effector, false, false, true, false);
        return true;
    }
}

