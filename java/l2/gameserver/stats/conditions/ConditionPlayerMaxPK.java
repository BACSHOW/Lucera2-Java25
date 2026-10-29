/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerMaxPK
extends Condition {
    private final int Df;

    public ConditionPlayerMaxPK(int n) {
        this.Df = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character.isPlayer()) {
            return ((Player)env.character).getPkKills() <= this.Df;
        }
        return false;
    }
}

