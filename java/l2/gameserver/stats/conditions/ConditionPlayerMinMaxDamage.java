/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerMinMaxDamage
extends Condition {
    private final double apM;
    private final double ahK;

    public ConditionPlayerMinMaxDamage(double d, double d2) {
        this.apM = d;
        this.ahK = d2;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (this.apM > 0.0 && env.value < this.apM) {
            return false;
        }
        return !(this.ahK > 0.0) || !(env.value > this.ahK);
    }
}

