/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectInvisible
extends Effect {
    private InvisibleType Jo = InvisibleType.NONE;

    public EffectInvisible(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (!this._effected.isPlayer()) {
            return false;
        }
        Player player = (Player)this._effected;
        if (player.isInvisible()) {
            return false;
        }
        if (player.getActiveWeaponFlagAttachment() != null) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = (Player)this._effected;
        this.Jo = player.getInvisibleType();
        player.setInvisibleType(InvisibleType.EFFECT);
        World.removeObjectFromPlayers(player);
    }

    @Override
    public void onExit() {
        super.onExit();
        Player player = (Player)this._effected;
        if (!player.isInvisible()) {
            return;
        }
        player.setInvisibleType(this.Jo);
        player.broadcastUserInfo(true);
        if (player.getPet() != null) {
            player.getPet().broadcastCharInfo();
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

