/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionFirstEffectSuccess
extends Condition {
    boolean _param;

    public ConditionFirstEffectSuccess(boolean bl) {
        this._param = bl;
    }

    @Override
    protected boolean testImpl(Env env) {
        return this._param == (env.value == 2.147483647E9);
    }
}

