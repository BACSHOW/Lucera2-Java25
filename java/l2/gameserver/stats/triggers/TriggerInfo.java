/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.triggers;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.triggers.TriggerType;

public class TriggerInfo
extends Skill.AddedSkill {
    private final TriggerType aqB;
    private final double hc;
    private Condition[] _conditions = Condition.EMPTY_ARRAY;

    public TriggerInfo(int n, int n2, TriggerType triggerType, double d) {
        super(n, n2);
        this.aqB = triggerType;
        this.hc = d;
    }

    public final void addCondition(Condition condition) {
        this._conditions = ArrayUtils.add(this._conditions, condition);
    }

    public boolean checkCondition(Creature creature, Creature creature2, Creature creature3, Skill skill, double d) {
        if (this.getSkill().checkTarget(creature, creature3, creature3, false, false) != null) {
            return false;
        }
        Env env = new Env();
        env.character = creature;
        env.skill = skill;
        env.target = creature2;
        env.value = d;
        for (Condition condition : this._conditions) {
            if (condition.test(env)) continue;
            return false;
        }
        return true;
    }

    public TriggerType getType() {
        return this.aqB;
    }

    public double getChance() {
        return this.hc;
    }
}

