/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.skills.skillclasses.NegateStats;
import l2.gameserver.stats.Env;

public class EffectBlockStat
extends Effect {
    public EffectBlockStat(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        this._effected.addBlockStats(((NegateStats)this._skill).getNegateStats());
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.removeBlockStats(((NegateStats)this._skill).getNegateStats());
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

