/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Zone;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionZoneType
extends Condition {
    private final Zone.ZoneType aqz;

    public ConditionZoneType(String string) {
        this.aqz = Zone.ZoneType.valueOf(string);
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer()) {
            return false;
        }
        return env.character.isInZone(this.aqz);
    }
}

