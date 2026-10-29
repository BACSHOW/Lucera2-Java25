/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public final class ConditionHasSkill
extends Condition {
    private final Integer apT;
    private final int _level;

    public ConditionHasSkill(Integer n, int n2) {
        this.apT = n;
        this._level = n2;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.skill == null) {
            return false;
        }
        return env.character.getSkillLevel(this.apT) >= this._level;
    }
}

