/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.Warehouse;

public class PcFreight
extends Warehouse {
    public PcFreight(Player player) {
        super(player.getObjectId());
    }

    public PcFreight(int n) {
        super(n);
    }

    @Override
    public ItemInstance.ItemLocation getItemLocation() {
        return ItemInstance.ItemLocation.FREIGHT;
    }
}

