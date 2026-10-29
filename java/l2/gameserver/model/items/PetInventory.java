/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import java.util.Collection;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PetInventoryUpdate;
import l2.gameserver.utils.ItemFunctions;

public class PetInventory
extends Inventory {
    private final PetInstance XJ;

    public PetInventory(PetInstance petInstance) {
        super(petInstance.getPlayer().getObjectId());
        this.XJ = petInstance;
    }

    @Override
    public PetInstance getActor() {
        return this.XJ;
    }

    public Player getOwner() {
        return this.XJ.getPlayer();
    }

    @Override
    protected ItemInstance.ItemLocation getBaseLocation() {
        return ItemInstance.ItemLocation.PET_INVENTORY;
    }

    @Override
    protected ItemInstance.ItemLocation getEquipLocation() {
        return ItemInstance.ItemLocation.PET_PAPERDOLL;
    }

    @Override
    protected void onRefreshWeight() {
        this.getActor().sendPetInfo();
    }

    @Override
    protected void sendAddItem(ItemInstance itemInstance) {
        this.getOwner().sendPacket((IStaticPacket)new PetInventoryUpdate().addNewItem(itemInstance));
    }

    @Override
    protected void sendModifyItem(ItemInstance itemInstance) {
        this.getOwner().sendPacket((IStaticPacket)new PetInventoryUpdate().addModifiedItem(itemInstance));
    }

    @Override
    protected void sendRemoveItem(ItemInstance itemInstance) {
        this.getOwner().sendPacket((IStaticPacket)new PetInventoryUpdate().addRemovedItem(itemInstance));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void restore() {
        int n = this.getOwnerId();
        this.writeLock();
        try {
            Collection<ItemInstance> collection = _itemsDAO.loadItemsByOwnerIdAndLoc(n, this.getBaseLocation());
            for (ItemInstance itemInstance : collection) {
                this._items.add(itemInstance);
                this.onRestoreItem(itemInstance);
            }
            collection = _itemsDAO.loadItemsByOwnerIdAndLoc(n, this.getEquipLocation());
            for (ItemInstance itemInstance : collection) {
                this._items.add(itemInstance);
                this.onRestoreItem(itemInstance);
                if (ItemFunctions.checkIfCanEquip(this.getActor(), itemInstance) != null) continue;
                this.setPaperdollItem(itemInstance.getEquipSlot(), itemInstance);
            }
        }
        finally {
            this.writeUnlock();
        }
        this.refreshWeight();
    }

    @Override
    public void store() {
        this.writeLock();
        try {
            _itemsDAO.store(this._items);
        }
        finally {
            this.writeUnlock();
        }
    }

    public void validateItems() {
        for (ItemInstance itemInstance : this._paperdoll) {
            if (itemInstance == null || ItemFunctions.checkIfCanEquip(this.getActor(), itemInstance) == null && itemInstance.getTemplate().testCondition(this.getActor(), itemInstance, false)) continue;
            this.unEquipItem(itemInstance);
        }
    }
}

