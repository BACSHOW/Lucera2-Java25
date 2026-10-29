/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerAgathion
extends Condition {
    private final int Ip;

    public ConditionPlayerAgathion(int n) {
        this.Ip = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer()) {
            return false;
        }
        if (((Player)env.character).getAgathionId() > 0 && this.Ip == -1) {
            return true;
        }
        return ((Player)env.character).getAgathionId() == this.Ip;
    }
}

