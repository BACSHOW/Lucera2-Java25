/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Player;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public class ConditionPlayerClassId
extends Condition {
    private final int[] apY;

    public ConditionPlayerClassId(String[] stringArray) {
        this.apY = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; ++i) {
            this.apY[i] = Integer.parseInt(stringArray[i]);
        }
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer()) {
            return false;
        }
        int n = ((Player)env.character).getActiveClassId();
        for (int n2 : this.apY) {
            if (n != n2) continue;
            return true;
        }
        return false;
    }
}

