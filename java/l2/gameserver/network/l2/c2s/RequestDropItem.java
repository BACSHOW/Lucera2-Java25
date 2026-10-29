/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.utils.Location;

public class RequestDropItem
extends L2GameClientPacket {
    private int CS;
    private long CE;
    private Location cH;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.CE = this.readD();
        this.cH = new Location(this.readD(), this.readD(), this.readD());
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (this.CE < 1L || this.cH.isNull()) {
            player.sendActionFailed();
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (!Config.ALLOW_DISCARDITEM || player.getPlayerAccess().BlockInventory) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestDropItem.Disallowed", player, new Object[0]));
            return;
        }
        if (player.isInStoreMode()) {
            player.sendPacket((IStaticPacket)Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (player.isSitting() || player.isDropDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInTrade()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        if (player.isActionBlocked("drop_item")) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DISCARD_THOSE_ITEMS_HERE);
            return;
        }
        if (player.isTradeBannedByGM()) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            return;
        }
        if (!player.isInRangeSq(this.cH, 22500L) || Math.abs(this.cH.z - player.getZ()) > 50) {
            player.sendPacket((IStaticPacket)Msg.THAT_IS_TOO_FAR_FROM_YOU_TO_DISCARD);
            return;
        }
        ItemInstance itemInstance = player.getInventory().getItemByObjectId(this.CS);
        if (itemInstance == null) {
            player.sendActionFailed();
            return;
        }
        if (!itemInstance.canBeDropped(player, false)) {
            player.sendPacket((IStaticPacket)Msg.THAT_ITEM_CANNOT_BE_DISCARDED);
            return;
        }
        itemInstance.getTemplate().getHandler().dropItem(player, itemInstance, this.CE, this.cH);
    }
}

