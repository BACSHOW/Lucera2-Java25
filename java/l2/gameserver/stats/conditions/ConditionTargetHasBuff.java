/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public final class ConditionTargetHasBuff
extends Condition {
    private final EffectType _effectType;
    private final int _level;

    public ConditionTargetHasBuff(EffectType effectType, int n) {
        this._effectType = effectType;
        this._level = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature creature = env.target;
        if (creature == null) {
            return false;
        }
        Effect effect = creature.getEffectList().getEffectByType(this._effectType);
        if (effect == null) {
            return false;
        }
        return this._level == -1 || effect.getSkill().getLevel() >= this._level;
    }
}

