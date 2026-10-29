/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncOwner;

public final class Calculator {
    private Func[] apJ;
    private double apK;
    private double apL;
    public final Stats _stat;
    public final Creature _character;

    public Calculator(Stats stats, Creature creature) {
        this._stat = stats;
        this._character = creature;
        this.apJ = Func.EMPTY_FUNC_ARRAY;
    }

    public int size() {
        return this.apJ.length;
    }

    public void addFunc(Func func) {
        this.apJ = ArrayUtils.add(this.apJ, func);
        ArrayUtils.eqSort((Comparable[])this.apJ);
    }

    public void removeFunc(Func func) {
        this.apJ = ArrayUtils.remove(this.apJ, func);
        if (this.apJ.length == 0) {
            this.apJ = Func.EMPTY_FUNC_ARRAY;
        } else {
            ArrayUtils.eqSort((Comparable[])this.apJ);
        }
    }

    public void removeOwner(Object object) {
        Func[] funcArray;
        for (Func func : funcArray = this.apJ) {
            if (func.owner != object) continue;
            this.removeFunc(func);
        }
    }

    public void calc(Env env) {
        Func[] funcArray = this.apJ;
        this.apK = env.value;
        boolean bl = false;
        for (Func func : funcArray) {
            if (func == null) continue;
            if (func.owner instanceof FuncOwner) {
                if (!((FuncOwner)func.owner).isFuncEnabled()) continue;
                if (((FuncOwner)func.owner).overrideLimits()) {
                    bl = true;
                }
            }
            if (func.getCondition() != null && !func.getCondition().test(env)) continue;
            func.calc(env);
        }
        if (!bl) {
            env.value = this._stat.validate(env.value);
        }
        if (env.value != this.apL) {
            double d = this.apL;
            this.apL = env.value;
        }
    }

    public Func[] getFunctions() {
        return this.apJ;
    }

    public double getBase() {
        return this.apK;
    }

    public double getLast() {
        return this.apL;
    }
}

