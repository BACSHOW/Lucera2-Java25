/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionTargetClan
extends Condition {
    private final boolean aqq;

    public ConditionTargetClan(String string) {
        this.aqq = Boolean.valueOf(string);
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature creature = env.character;
        Creature creature2 = env.target;
        return creature.getPlayer() != null && creature2.getPlayer() != null && (creature.getPlayer().getClanId() != 0 && creature.getPlayer().getClanId() == creature2.getPlayer().getClanId() == this.aqq || creature.getPlayer().getParty() != null && creature.getPlayer().getParty() == creature2.getPlayer().getParty());
    }
}

