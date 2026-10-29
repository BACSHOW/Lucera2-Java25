/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Summon;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;

public class EffectServitorShare
extends Effect {
    public EffectServitorShare(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    protected void onStart() {
        Summon summon;
        super.onStart();
        if (this._effector.isPlayer() && !this._effector.isAlikeDead() && (summon = this._effector.getPet()) != null && !summon.isAlikeDead()) {
            summon.addStatFuncs(this.dv());
        }
    }

    @Override
    protected void onExit() {
        Summon summon;
        if (this._effector.isPlayer() && !this._effector.isAlikeDead() && (summon = this._effector.getPet()) != null && !summon.isAlikeDead()) {
            summon.removeStatsOwner(this);
        }
        super.onExit();
    }

    @Override
    public Func[] getStatFuncs() {
        return Func.EMPTY_FUNC_ARRAY;
    }

    private Func[] dv() {
        FuncTemplate[] funcTemplateArray = this.getTemplate().getAttachedFuncs();
        Func[] funcArray = new Func[funcTemplateArray.length];
        for (int i = 0; i < funcArray.length; ++i) {
            funcArray[i] = new FuncShare(funcTemplateArray[i]._stat, funcTemplateArray[i]._order, this, funcTemplateArray[i]._value);
        }
        return funcArray;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }

    public class FuncShare
    extends Func {
        public FuncShare(Stats stats, int n, Object object, double d) {
            super(stats, n, object, d);
        }

        @Override
        public void calc(Env env) {
            env.value += env.character.getPlayer().calcStat(this.stat, this.stat.getInit()) * this.value;
        }
    }
}

