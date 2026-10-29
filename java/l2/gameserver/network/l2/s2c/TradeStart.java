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

public class TradeStart
extends L2GameServerPacket {
    private List<ItemInfo> ans = new ArrayList<ItemInfo>();
    private int ant;

    public TradeStart(Player player, Player player2) {
        ItemInstance[] itemInstanceArray;
        this.ant = player2.getObjectId();
        for (ItemInstance itemInstance : itemInstanceArray = player.getInventory().getItems()) {
            if (!itemInstance.canBeTraded(player)) continue;
            this.ans.add(new ItemInfo(itemInstance));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(30);
        this.writeD(this.ant);
        this.writeH(this.ans.size());
        for (ItemInfo itemInfo : this.ans) {
            this.writeH(itemInfo.getItem().getType1());
            this.writeD(itemInfo.getObjectId());
            this.writeD(itemInfo.getItemId());
            this.writeD((int)itemInfo.getCount());
            this.writeH(itemInfo.getItem().getType2ForPackets());
            this.writeH(0);
            this.writeD(itemInfo.getItem().getBodyPart());
            this.writeH(itemInfo.getEnchantLevel());
            this.writeH(0);
            this.writeH(0);
        }
    }
}

