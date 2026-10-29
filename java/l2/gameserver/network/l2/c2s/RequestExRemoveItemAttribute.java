/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemAttributes;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.ExBaseAttributeCancelResult;
import l2.gameserver.network.l2.s2c.ExShowBaseAttributeCancelWindow;
import l2.gameserver.network.l2.s2c.InventoryUpdate;

public class RequestExRemoveItemAttribute
extends L2GameClientPacket {
    private int CS;
    private int abT;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.abT = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled() || player.isInStoreMode() || player.isInTrade()) {
            player.sendActionFailed();
            return;
        }
        PcInventory pcInventory = player.getInventory();
        ItemInstance itemInstance = pcInventory.getItemByObjectId(this.CS);
        if (itemInstance == null) {
            player.sendActionFailed();
            return;
        }
        ItemAttributes itemAttributes = itemInstance.getAttributes();
        Element element = Element.getElementById(this.abT);
        if (element == Element.NONE || itemAttributes.getValue(element) <= 0) {
            player.sendPacket(new ExBaseAttributeCancelResult(false, itemInstance, element), ActionFail.STATIC);
            return;
        }
        if (!player.reduceAdena(ExShowBaseAttributeCancelWindow.getAttributeRemovePrice(itemInstance), true)) {
            player.sendPacket(new ExBaseAttributeCancelResult(false, itemInstance, element), SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, ActionFail.STATIC);
            return;
        }
        boolean bl = false;
        bl = itemInstance.isEquipped();
        if (bl) {
            player.getInventory().unEquipItem(itemInstance);
        }
        itemInstance.setAttributeElement(element, 0);
        if (bl) {
            player.getInventory().equipItem(itemInstance);
        }
        player.sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(itemInstance));
        player.sendPacket((IStaticPacket)new ExBaseAttributeCancelResult(true, itemInstance, element));
        player.updateStats();
    }
}

