/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.Warehouse;

public class PcWarehouse
extends Warehouse {
    public PcWarehouse(Player player) {
        super(player.getObjectId());
    }

    public PcWarehouse(int n) {
        super(n);
    }

    @Override
    public ItemInstance.ItemLocation getItemLocation() {
        return ItemInstance.ItemLocation.WAREHOUSE;
    }
}

