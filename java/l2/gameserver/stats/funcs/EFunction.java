/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.funcs;

import java.util.HashMap;
import java.util.Map;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncAdd;
import l2.gameserver.stats.funcs.FuncDiv;
import l2.gameserver.stats.funcs.FuncEnchant;
import l2.gameserver.stats.funcs.FuncMul;
import l2.gameserver.stats.funcs.FuncSet;
import l2.gameserver.stats.funcs.FuncSub;

public enum EFunction {
    Set{

        @Override
        public Func create(Stats stats, int n, Object object, double d) {
            return new FuncSet(stats, n, object, d);
        }
    }
    ,
    Add{

        @Override
        public Func create(Stats stats, int n, Object object, double d) {
            return new FuncAdd(stats, n, object, d);
        }
    }
    ,
    Sub{

        @Override
        public Func create(Stats stats, int n, Object object, double d) {
            return new FuncSub(stats, n, object, d);
        }
    }
    ,
    Mul{

        @Override
        public Func create(Stats stats, int n, Object object, double d) {
            return new FuncMul(stats, n, object, d);
        }
    }
    ,
    Div{

        @Override
        public Func create(Stats stats, int n, Object object, double d) {
            return new FuncDiv(stats, n, object, d);
        }
    }
    ,
    Enchant{

        @Override
        public Func create(Stats stats, int n, Object object, double d) {
            return new FuncEnchant(stats, n, object, d);
        }
    };

    public static final EFunction[] VALUES;
    public static final Map<String, EFunction> VALUES_BY_LOWER_NAME;

    public abstract Func create(Stats var1, int var2, Object var3, double var4);

    static {
        VALUES = EFunction.values();
        VALUES_BY_LOWER_NAME = new HashMap<String, EFunction>();
        for (EFunction eFunction : VALUES) {
            VALUES_BY_LOWER_NAME.put(eFunction.name().toLowerCase(), eFunction);
        }
    }
}

