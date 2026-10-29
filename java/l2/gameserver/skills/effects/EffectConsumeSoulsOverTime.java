/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectConsumeSoulsOverTime
extends Effect {
    public EffectConsumeSoulsOverTime(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean onActionTime() {
        if (this._effected.isDead()) {
            return false;
        }
        if (this._effected.getConsumedSouls() < 0) {
            return false;
        }
        int n = (int)this.calc();
        if (this._effected.getConsumedSouls() < n) {
            this._effected.setConsumedSouls(0, null);
        } else {
            this._effected.setConsumedSouls(this._effected.getConsumedSouls() - n, null);
        }
        return true;
    }
}

