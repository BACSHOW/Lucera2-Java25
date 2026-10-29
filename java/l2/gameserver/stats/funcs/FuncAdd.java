/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.funcs;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;

public class FuncAdd
extends Func {
    public FuncAdd(Stats stats, int n, Object object, double d) {
        super(stats, n, object, d);
    }

    @Override
    public void calc(Env env) {
        env.value += this.value;
    }
}

