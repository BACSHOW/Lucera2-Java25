/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public final class ConditionTargetHasForbiddenSkill
extends Condition {
    private final int _skillId;

    public ConditionTargetHasForbiddenSkill(int n) {
        this._skillId = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature creature = env.target;
        if (!creature.isPlayable()) {
            return false;
        }
        return creature.getSkillLevel(this._skillId) <= 0;
    }
}

