/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectEnervation
extends Effect {
    public EffectEnervation(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this._effected.isNpc()) {
            ((NpcInstance)this._effected).setParameter("DebuffIntention", 0.5);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }

    @Override
    public void onExit() {
        super.onExit();
        if (this._effected.isNpc()) {
            ((NpcInstance)this._effected).setParameter("DebuffIntention", 1.0);
        }
    }
}

