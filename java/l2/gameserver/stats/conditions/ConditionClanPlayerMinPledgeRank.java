/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionClanPlayerMinPledgeRank
extends Condition {
    private final Player.EPledgeRank apP;

    private static Player.EPledgeRank y(String string) {
        Player.EPledgeRank ePledgeRank = Player.EPledgeRank.valueOf(string.toUpperCase());
        if (ePledgeRank == null) {
            throw new IllegalArgumentException("Unknown pledge rank \"" + string + "\"");
        }
        return ePledgeRank;
    }

    public ConditionClanPlayerMinPledgeRank(String string) {
        this(ConditionClanPlayerMinPledgeRank.y(string));
    }

    public ConditionClanPlayerMinPledgeRank(Player.EPledgeRank ePledgeRank) {
        this.apP = ePledgeRank;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character == null) {
            return false;
        }
        Player player = env.character.getPlayer();
        if (player == null) {
            return false;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            return false;
        }
        return player.getPledgeRank().getRankId() >= this.apP.getRankId();
    }
}

