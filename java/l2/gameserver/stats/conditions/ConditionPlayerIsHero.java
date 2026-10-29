/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerIsHero
extends Condition {
    private final boolean P;

    public ConditionPlayerIsHero(boolean bl) {
        this.P = bl;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character == null) {
            return false;
        }
        Player player = env.character.getPlayer();
        return player != null && player.isHero() == this.P;
    }
}

