/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.items.IRefineryHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;

public class RequestConfirmCancelItem
extends L2GameClientPacket {
    int _itemObjId;

    @Override
    protected void readImpl() {
        this._itemObjId = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByObjectId(this._itemObjId);
        IRefineryHandler iRefineryHandler = player.getRefineryHandler();
        if (itemInstance == null || iRefineryHandler == null) {
            player.sendActionFailed();
            player.sendPacket((IStaticPacket)Msg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        iRefineryHandler.onPutTargetCancelItem(player, itemInstance);
    }
}

