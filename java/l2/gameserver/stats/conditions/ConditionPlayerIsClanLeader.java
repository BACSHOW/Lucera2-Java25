/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerIsClanLeader
extends Condition {
    private final boolean P;

    public ConditionPlayerIsClanLeader(boolean bl) {
        this.P = bl;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character == null || !env.character.isPlayer()) {
            return false;
        }
        Player player = env.character.getPlayer();
        return player.isClanLeader() == this.P;
    }
}

