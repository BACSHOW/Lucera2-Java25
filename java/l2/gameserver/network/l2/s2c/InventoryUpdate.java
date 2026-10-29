/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class InventoryUpdate
extends L2GameServerPacket {
    public static final int UNCHANGED = 0;
    public static final int ADDED = 1;
    public static final int MODIFIED = 2;
    public static final int REMOVED = 3;
    private final List<ItemInfo> _items = new ArrayList<ItemInfo>(1);

    public InventoryUpdate addNewItem(ItemInstance itemInstance) {
        this.a(itemInstance).setLastChange(1);
        return this;
    }

    public InventoryUpdate addModifiedItem(ItemInstance itemInstance) {
        this.a(itemInstance).setLastChange(2);
        return this;
    }

    public InventoryUpdate addRemovedItem(ItemInstance itemInstance) {
        this.a(itemInstance).setLastChange(3);
        return this;
    }

    private ItemInfo a(ItemInstance itemInstance) {
        ItemInfo itemInfo = new ItemInfo(itemInstance);
        this._items.add(itemInfo);
        return itemInfo;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(39);
        this.writeH(this._items.size());
        for (ItemInfo itemInfo : this._items) {
            this.writeH(itemInfo.getLastChange());
            this.writeItemInfo(itemInfo);
        }
    }
}

