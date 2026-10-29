/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerCubic
extends Condition {
    private int _id;

    public ConditionPlayerCubic(int n) {
        this._id = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.target == null || !env.target.isPlayer()) {
            return false;
        }
        Player player = (Player)env.target;
        if (player.getCubic(this._id) != null) {
            return true;
        }
        int n = (int)player.calcStat(Stats.CUBICS_LIMIT, 1.0);
        if (player.getCubics().size() >= n) {
            if (env.character == player) {
                player.sendPacket((IStaticPacket)Msg.CUBIC_SUMMONING_FAILED);
            }
            return false;
        }
        return true;
    }
}

