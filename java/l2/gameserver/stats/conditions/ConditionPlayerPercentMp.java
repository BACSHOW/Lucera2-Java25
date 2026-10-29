/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerPercentMp
extends Condition {
    private final double aqf;

    public ConditionPlayerPercentMp(int n) {
        this.aqf = (double)n / 100.0;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.getCurrentMpRatio() <= this.aqf;
    }
}

