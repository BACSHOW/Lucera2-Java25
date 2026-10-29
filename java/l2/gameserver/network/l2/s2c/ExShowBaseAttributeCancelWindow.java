/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExShowBaseAttributeCancelWindow
extends L2GameServerPacket {
    private final List<ItemInstance> _items = new ArrayList<ItemInstance>();

    public ExShowBaseAttributeCancelWindow(Player player) {
        for (ItemInstance itemInstance : player.getInventory().getItems()) {
            if (itemInstance.getAttributeElement() == Element.NONE || !itemInstance.canBeEnchanted(true) || ExShowBaseAttributeCancelWindow.getAttributeRemovePrice(itemInstance) == 0L) continue;
            this._items.add(itemInstance);
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(116);
        this.writeD(this._items.size());
        for (ItemInstance itemInstance : this._items) {
            this.writeD(itemInstance.getObjectId());
            this.writeQ(ExShowBaseAttributeCancelWindow.getAttributeRemovePrice(itemInstance));
        }
    }

    public static long getAttributeRemovePrice(ItemInstance itemInstance) {
        switch (itemInstance.getCrystalType()) {
            case S: {
                return itemInstance.getTemplate().getType2() == 0 ? 50000L : 40000L;
            }
        }
        return 0L;
    }
}

