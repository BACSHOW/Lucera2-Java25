/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionTargetPercentMp
extends Condition {
    private final double aqf;

    public ConditionTargetPercentMp(int n) {
        this.aqf = (double)n / 100.0;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.target != null && env.target.getCurrentMpRatio() <= this.aqf;
    }
}

