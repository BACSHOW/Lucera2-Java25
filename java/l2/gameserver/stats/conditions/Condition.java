/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.stats.Env;

public abstract class Condition {
    public static final Condition[] EMPTY_ARRAY = new Condition[0];
    private SystemMsg _message;

    public final void setSystemMsg(int n) {
        this._message = SystemMsg.valueOf(n);
    }

    public final SystemMsg getSystemMsg() {
        return this._message;
    }

    public final boolean test(Env env) {
        return this.testImpl(env);
    }

    protected abstract boolean testImpl(Env var1);
}

