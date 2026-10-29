/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionUsingSkill
extends Condition {
    private int _id;

    public ConditionUsingSkill(int n) {
        this._id = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.skill == null) {
            return false;
        }
        return env.skill.getId() == this._id;
    }
}

