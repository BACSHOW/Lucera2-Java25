/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;

@Deprecated
public class RequestUnEquipItem
extends L2GameClientPacket {
    private int _slot;

    @Override
    protected void readImpl() {
        this._slot = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
            return;
        }
        if (!(this._slot != 128 && this._slot != 256 && this._slot != 16384 || !player.isCursedWeaponEquipped() && player.getActiveWeaponFlagAttachment() == null)) {
            return;
        }
        if (this._slot == 128) {
            ItemInstance itemInstance = player.getActiveWeaponInstance();
            if (itemInstance == null) {
                return;
            }
            player.abortAttack(true, true);
            player.abortCast(true, true);
            player.sendDisarmMessage(itemInstance);
        }
        player.getInventory().unEquipItemInBodySlot(this._slot);
    }
}

