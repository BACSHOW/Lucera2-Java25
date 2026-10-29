/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionTargetMobId
extends Condition {
    private final int aqu;

    public ConditionTargetMobId(int n) {
        this.aqu = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.target != null && env.target.getNpcId() == this.aqu;
    }
}

