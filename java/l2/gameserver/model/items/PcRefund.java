/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemContainer;
import l2.gameserver.model.items.ItemInstance;

public class PcRefund
extends ItemContainer {
    public PcRefund(Player player) {
    }

    @Override
    protected void onAddItem(ItemInstance itemInstance) {
        itemInstance.setLocation(ItemInstance.ItemLocation.VOID);
        itemInstance.save();
        if (this._items.size() > 12) {
            this.destroyItem((ItemInstance)this._items.remove(0));
        }
    }

    @Override
    protected void onModifyItem(ItemInstance itemInstance) {
        itemInstance.save();
    }

    @Override
    protected void onRemoveItem(ItemInstance itemInstance) {
        itemInstance.save();
    }

    @Override
    protected void onDestroyItem(ItemInstance itemInstance) {
        itemInstance.setCount(0L);
        itemInstance.delete();
    }

    @Override
    public void clear() {
        this.writeLock();
        try {
            _itemsDAO.delete(this._items);
            this._items.clear();
        }
        finally {
            this.writeUnlock();
        }
    }
}

