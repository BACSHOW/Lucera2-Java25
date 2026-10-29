/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.model.items.ItemContainer;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;

public abstract class Warehouse
extends ItemContainer {
    private static final ItemsDAO _itemsDAO = ItemsDAO.getInstance();
    protected final int _ownerId;

    protected Warehouse(int n) {
        this._ownerId = n;
    }

    public int getOwnerId() {
        return this._ownerId;
    }

    public abstract ItemInstance.ItemLocation getItemLocation();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ItemInstance[] getItems(ItemTemplate.ItemClass itemClass) {
        ArrayList<ItemInstance> arrayList = new ArrayList<ItemInstance>();
        this.readLock();
        try {
            for (int i = 0; i < this._items.size(); ++i) {
                ItemInstance itemInstance = (ItemInstance)this._items.get(i);
                if (itemClass != null && itemClass != ItemTemplate.ItemClass.ALL && itemInstance.getItemClass() != itemClass) continue;
                arrayList.add(itemInstance);
            }
        }
        finally {
            this.readUnlock();
        }
        return arrayList.toArray(new ItemInstance[arrayList.size()]);
    }

    public long getCountOfAdena() {
        return this.getCountOf(57);
    }

    @Override
    protected void onAddItem(ItemInstance itemInstance) {
        itemInstance.setOwnerId(this.getOwnerId());
        itemInstance.setLocation(this.getItemLocation());
        itemInstance.setLocData(0);
        itemInstance.save();
    }

    @Override
    protected void onModifyItem(ItemInstance itemInstance) {
        itemInstance.save();
    }

    @Override
    protected void onRemoveItem(ItemInstance itemInstance) {
        itemInstance.setLocData(-1);
        itemInstance.save();
    }

    @Override
    protected void onDestroyItem(ItemInstance itemInstance) {
        itemInstance.setCount(0L);
        itemInstance.delete();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void restore() {
        int n = this.getOwnerId();
        this.writeLock();
        try {
            Collection<ItemInstance> collection = _itemsDAO.loadItemsByOwnerIdAndLoc(n, this.getItemLocation());
            for (ItemInstance itemInstance : collection) {
                this._items.add(itemInstance);
            }
        }
        finally {
            this.writeUnlock();
        }
    }

    public static class ItemClassComparator
    implements Comparator<ItemInstance> {
        private static final Comparator<ItemInstance> tS = new ItemClassComparator();

        public static final Comparator<ItemInstance> getInstance() {
            return tS;
        }

        @Override
        public int compare(ItemInstance itemInstance, ItemInstance itemInstance2) {
            if (itemInstance == null || itemInstance2 == null) {
                return 0;
            }
            int n = itemInstance.getItemClass().ordinal() - itemInstance2.getItemClass().ordinal();
            if (n == 0) {
                n = itemInstance.getCrystalType().ordinal() - itemInstance2.getCrystalType().ordinal();
            }
            if (n == 0) {
                n = itemInstance.getItemId() - itemInstance2.getItemId();
            }
            if (n == 0) {
                n = itemInstance.getEnchantLevel() - itemInstance2.getEnchantLevel();
            }
            return n;
        }
    }

    public static enum WarehouseType {
        NONE,
        PRIVATE,
        CLAN,
        CASTLE,
        FREIGHT;

    }
}

