/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExQuestItemList
extends L2GameServerPacket {
    private int _size;
    private ItemInstance[] ahC;
    private LockType XG;
    private int[] XH;

    public ExQuestItemList(int n, ItemInstance[] itemInstanceArray, LockType lockType, int[] nArray) {
        this._size = n;
        this.ahC = itemInstanceArray;
        this.XG = lockType;
        this.XH = nArray;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(25);
        this.writeH(this._size);
        for (ItemInstance itemInstance : this.ahC) {
            if (!itemInstance.getTemplate().isQuest()) continue;
            this.writeItemInfo(itemInstance);
        }
        this.writeH(this.XH.length);
        if (this.XH.length > 0) {
            this.writeC(this.XG.ordinal());
            for (int n : this.XH) {
                this.writeD(n);
            }
        }
    }
}

