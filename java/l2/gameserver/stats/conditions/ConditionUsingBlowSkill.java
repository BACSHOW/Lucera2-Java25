/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionUsingBlowSkill
extends Condition {
    private final boolean _flag;

    public ConditionUsingBlowSkill(boolean bl) {
        this._flag = bl;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.skill == null) {
            return !this._flag;
        }
        return env.skill.isBlowSkill() == this._flag;
    }
}

