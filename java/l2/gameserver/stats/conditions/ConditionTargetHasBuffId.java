/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public final class ConditionTargetHasBuffId
extends Condition {
    private final int _id;
    private final int _level;

    public ConditionTargetHasBuffId(int n, int n2) {
        this._id = n;
        this._level = n2;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature creature = env.target;
        if (creature == null) {
            return false;
        }
        if (this._level == -1) {
            return creature.getEffectList().getEffectsBySkillId(this._id) != null;
        }
        List<Effect> list = creature.getEffectList().getEffectsBySkillId(this._id);
        if (list == null) {
            return false;
        }
        for (Effect effect : list) {
            if (effect == null || effect.getSkill().getLevel() < this._level) continue;
            return true;
        }
        return false;
    }
}

