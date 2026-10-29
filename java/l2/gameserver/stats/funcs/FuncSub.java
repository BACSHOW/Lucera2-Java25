/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.funcs;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;

public class FuncSub
extends Func {
    public FuncSub(Stats stats, int n, Object object, double d) {
        super(stats, n, object, d);
    }

    @Override
    public void calc(Env env) {
        switch (this.stat) {
            case MAX_CP: 
            case MAX_HP: 
            case MAX_MP: {
                env.value = Math.max(env.value - this.value, 1.0);
                break;
            }
            default: {
                env.value -= this.value;
            }
        }
    }
}

