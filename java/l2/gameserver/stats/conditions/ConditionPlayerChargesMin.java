/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerChargesMin
extends Condition {
    private final int apX;

    public ConditionPlayerChargesMin(int n) {
        this.apX = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character == null || !env.character.isPlayer()) {
            return false;
        }
        return ((Player)env.character).getIncreasedForce() >= this.apX;
    }
}

