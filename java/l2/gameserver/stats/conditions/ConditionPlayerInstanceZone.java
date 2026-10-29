/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.entity.Reflection;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerInstanceZone
extends Condition {
    private final int _id;

    public ConditionPlayerInstanceZone(int n) {
        this._id = n;
    }

    @Override
    protected boolean testImpl(Env env) {
        Reflection reflection = env.character.getReflection();
        return reflection.getInstancedZoneId() == this._id;
    }
}

