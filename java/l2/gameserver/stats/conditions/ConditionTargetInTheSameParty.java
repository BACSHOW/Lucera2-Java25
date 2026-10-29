/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionTargetInTheSameParty
extends Condition {
    private final boolean aqs;

    public ConditionTargetInTheSameParty(boolean bl) {
        this.aqs = bl;
    }

    @Override
    protected boolean testImpl(Env env) {
        Player player;
        Creature creature = env.character;
        Creature creature2 = env.target;
        if (!creature.isPlayable() || creature2 == null || !creature2.isPlayable()) {
            return !this.aqs;
        }
        Player player2 = creature.getPlayer();
        if (player2 == (player = creature2.getPlayer())) {
            return this.aqs;
        }
        if (player2.isInParty() && player2.getParty() == player.getParty()) {
            return this.aqs;
        }
        return !this.aqs;
    }
}

