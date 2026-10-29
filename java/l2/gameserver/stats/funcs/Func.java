/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.funcs;

import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;

public abstract class Func
implements Comparable<Func> {
    public static final Func[] EMPTY_FUNC_ARRAY = new Func[0];
    public final Stats stat;
    public final int order;
    public final Object owner;
    public final double value;
    protected Condition cond;

    public Func(Stats stats, int n, Object object) {
        this(stats, n, object, 0.0);
    }

    public Func(Stats stats, int n, Object object, double d) {
        this.stat = stats;
        this.order = n;
        this.owner = object;
        this.value = d;
    }

    public void setCondition(Condition condition) {
        this.cond = condition;
    }

    public Condition getCondition() {
        return this.cond;
    }

    public abstract void calc(Env var1);

    @Override
    public int compareTo(Func func) throws NullPointerException {
        return this.order - func.order;
    }
}

