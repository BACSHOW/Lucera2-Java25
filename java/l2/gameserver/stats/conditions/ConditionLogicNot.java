/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionLogicNot
extends Condition {
    private final Condition apV;

    public ConditionLogicNot(Condition condition) {
        this.apV = condition;
    }

    @Override
    protected boolean testImpl(Env env) {
        return !this.apV.test(env);
    }
}

