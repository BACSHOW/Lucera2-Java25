/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.funcs;

import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.funcs.EFunction;
import l2.gameserver.stats.funcs.Func;

public final class FuncTemplate {
    public static final FuncTemplate[] EMPTY_ARRAY = new FuncTemplate[0];
    public Condition _applyCond;
    public EFunction _func;
    public Stats _stat;
    public int _order;
    public double _value;

    public FuncTemplate(Condition condition, String string, Stats stats, int n, double d) {
        this._applyCond = condition;
        this._stat = stats;
        this._order = n;
        this._value = d;
        this._func = EFunction.VALUES_BY_LOWER_NAME.get(string.toLowerCase());
        if (this._func == null) {
            throw new RuntimeException("Unknown function " + string);
        }
    }

    public FuncTemplate(Condition condition, EFunction eFunction, Stats stats, int n, double d) {
        this._applyCond = condition;
        this._stat = stats;
        this._order = n;
        this._value = d;
        this._func = eFunction;
    }

    public Func getFunc(Object object) {
        Func func = this._func.create(this._stat, this._order, object, this._value);
        if (this._applyCond != null) {
            func.setCondition(this._applyCond);
        }
        return func;
    }
}

