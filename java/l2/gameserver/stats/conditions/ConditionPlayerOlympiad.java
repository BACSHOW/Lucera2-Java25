/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerOlympiad
extends Condition {
    private final boolean P;

    public ConditionPlayerOlympiad(boolean bl) {
        this.P = bl;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.isOlyParticipant() == this.P;
    }
}

