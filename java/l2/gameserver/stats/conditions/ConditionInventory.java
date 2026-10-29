/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;

public abstract class ConditionInventory
extends Condition {
    protected final int _slot;

    public ConditionInventory(int n) {
        this._slot = n;
    }

    @Override
    protected abstract boolean testImpl(Env var1);
}

