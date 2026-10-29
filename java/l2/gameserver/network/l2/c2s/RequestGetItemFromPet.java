/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.PetInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.utils.Log;

public class RequestGetItemFromPet
extends L2GameClientPacket {
    private int CS;
    private long aav;
    private int aaM;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.aav = this.readD();
        this.aaM = this.readD();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this.aav < 1L) {
            return;
        }
        PetInstance petInstance = (PetInstance)player.getPet();
        if (petInstance == null) {
            player.sendActionFailed();
            return;
        }
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendPacket((IStaticPacket)SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (player.isInTrade() || player.isProcessingRequest()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_);
            return;
        }
        PetInventory petInventory = petInstance.getInventory();
        PcInventory pcInventory = player.getInventory();
        petInventory.writeLock();
        pcInventory.writeLock();
        try {
            ItemInstance itemInstance = petInventory.getItemByObjectId(this.CS);
            if (itemInstance == null || itemInstance.getCount() < this.aav || itemInstance.isEquipped()) {
                player.sendActionFailed();
                return;
            }
            int n = 0;
            long l = (long)itemInstance.getTemplate().getWeight() * this.aav;
            if (!itemInstance.getTemplate().isStackable() || player.getInventory().getItemByItemId(itemInstance.getItemId()) == null) {
                n = 1;
            }
            if (!player.getInventory().validateWeight(l)) {
                player.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }
            if (!player.getInventory().validateCapacity(n)) {
                player.sendPacket((IStaticPacket)SystemMsg.YOUR_INVENTORY_IS_FULL);
                return;
            }
            itemInstance = petInventory.removeItemByObjectId(this.CS, this.aav);
            Log.LogItem(player, Log.ItemLog.FromPet, itemInstance);
            pcInventory.addItem(itemInstance);
            petInstance.sendChanges();
            player.sendChanges();
        }
        finally {
            pcInventory.writeUnlock();
            petInventory.writeUnlock();
        }
    }
}

