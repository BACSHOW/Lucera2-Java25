/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerOnPvPEvent
extends Condition {
    private final boolean P;

    public ConditionPlayerOnPvPEvent(boolean bl) {
        this.P = bl;
    }

    private boolean ae(Player player) {
        return (Boolean)Scripts.getInstance().callScripts(player, "events.TvT2.PvPEvent", "isEventPartisipant") != false;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character == null) {
            return false;
        }
        Player player = env.character.getPlayer();
        return player != null && this.ae(player) == this.P;
    }
}

