/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerResidence
extends Condition {
    private final int _id;
    private final ResidenceType aqh;

    public ConditionPlayerResidence(int n, ResidenceType residenceType) {
        this._id = n;
        this.aqh = residenceType;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer()) {
            return false;
        }
        Player player = (Player)env.character;
        Clan clan = player.getClan();
        if (clan == null) {
            return false;
        }
        int n = clan.getResidenceId(this.aqh);
        return this._id > 0 ? n == this._id : n > 0;
    }
}

