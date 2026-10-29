/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectBlessNoblesse
extends Effect {
    public EffectBlessNoblesse(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        Creature creature = this.getEffected();
        if (creature.isPlayer() || Config.ALT_SAVE_SERVITOR_BUFF) {
            creature.setIsBlessedByNoblesse(true);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        this.getEffected().setIsBlessedByNoblesse(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

