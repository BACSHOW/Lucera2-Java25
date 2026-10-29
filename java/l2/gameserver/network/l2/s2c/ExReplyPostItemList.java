/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExReplyPostItemList
extends L2GameServerPacket {
    private List<ItemInfo> ahL = new ArrayList<ItemInfo>();

    public ExReplyPostItemList(Player player) {
        ItemInstance[] itemInstanceArray;
        for (ItemInstance itemInstance : itemInstanceArray = player.getInventory().getItems()) {
            if (!itemInstance.canBeTraded(player)) continue;
            this.ahL.add(new ItemInfo(itemInstance));
        }
    }

    @Override
    protected void writeImpl() {
        this.writeEx(178);
        this.writeD(this.ahL.size());
        for (ItemInfo itemInfo : this.ahL) {
            this.writeItemInfo(itemInfo);
        }
    }
}

