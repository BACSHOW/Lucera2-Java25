/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionTargetPlayerRace
extends Condition {
    private final Race Oc;

    public ConditionTargetPlayerRace(String string) {
        this.Oc = Race.valueOf(string.toLowerCase());
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature creature = env.target;
        return creature != null && creature.isPlayer() && this.Oc == ((Player)creature).getRace();
    }
}

