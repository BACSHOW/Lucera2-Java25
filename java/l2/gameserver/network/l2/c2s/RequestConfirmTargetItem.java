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
import l2.gameserver.network.l2.s2c.ExPutItemResultForVariationMake;

public class RequestConfirmTargetItem
extends L2GameClientPacket {
    private int _itemObjId;

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
            player.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ExPutItemResultForVariationMake.FAIL_PACKET);
            return;
        }
        iRefineryHandler.onPutTargetItem(player, itemInstance);
    }
}

