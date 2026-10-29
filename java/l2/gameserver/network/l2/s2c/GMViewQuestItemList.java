/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class GMViewQuestItemList
extends L2GameServerPacket {
    private int _size;
    private ItemInstance[] ahC;
    private int KH;
    private String _name;

    public GMViewQuestItemList(Player player, ItemInstance[] itemInstanceArray, int n) {
        this.ahC = itemInstanceArray;
        this._size = n;
        this._name = player.getName();
        this.KH = player.getInventoryLimit();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(147);
        this.writeS(this._name);
        this.writeD(this.KH);
        this.writeH(1);
        this.writeH(this._size);
        for (ItemInstance itemInstance : this.ahC) {
            if (!itemInstance.getTemplate().isQuest()) continue;
            this.writeItemInfo(itemInstance);
        }
    }
}

