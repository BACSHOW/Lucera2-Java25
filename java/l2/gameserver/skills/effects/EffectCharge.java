/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectCharge
extends Effect {
    public static final int MAX_CHARGE = 7;
    private final int aor;
    private final boolean aos;

    public EffectCharge(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aor = effectTemplate.getParam().getInteger("charges", 7);
        this.aos = effectTemplate.getParam().getBool("fullCharge", false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.getEffected().isPlayer()) {
            Player player = (Player)this.getEffected();
            if (player.getIncreasedForce() >= this.aor) {
                player.sendPacket((IStaticPacket)Msg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
            } else if (this.aos) {
                player.setIncreasedForce(this.aor);
            } else {
                player.setIncreasedForce(player.getIncreasedForce() + 1);
            }
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

