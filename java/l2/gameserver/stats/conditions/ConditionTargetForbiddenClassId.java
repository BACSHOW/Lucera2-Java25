/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import gnu.trove.TIntHashSet;
import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionTargetForbiddenClassId
extends Condition {
    private TIntHashSet apZ = new TIntHashSet();

    public ConditionTargetForbiddenClassId(String[] stringArray) {
        for (String string : stringArray) {
            this.apZ.add(Integer.parseInt(string));
        }
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature creature = env.target;
        if (creature == null || !creature.isPlayable()) {
            return false;
        }
        return !creature.isPlayer() || !this.apZ.contains(creature.getPlayer().getActiveClassId());
    }
}

