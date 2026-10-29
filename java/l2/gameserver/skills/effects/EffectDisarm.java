/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectDisarm
extends Effect {
    public EffectDisarm(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        if (!this._effected.isPlayer()) {
            return false;
        }
        Player player = this._effected.getPlayer();
        if (player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = (Player)this._effected;
        ItemInstance itemInstance = player.getActiveWeaponInstance();
        if (itemInstance != null) {
            player.getInventory().unEquipItem(itemInstance);
            player.sendDisarmMessage(itemInstance);
        }
        player.startWeaponEquipBlocked();
    }

    @Override
    public void onExit() {
        super.onExit();
        this._effected.stopWeaponEquipBlocked();
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

