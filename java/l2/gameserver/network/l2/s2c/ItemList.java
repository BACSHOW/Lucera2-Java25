/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ItemList
extends L2GameServerPacket {
    private final int _size;
    private final ItemInstance[] ahC;
    private final boolean aju;
    private LockType XG;
    private int[] XH;

    public ItemList(int n, ItemInstance[] itemInstanceArray, boolean bl, LockType lockType, int[] nArray) {
        this._size = n;
        this.ahC = itemInstanceArray;
        this.aju = bl;
        this.XG = lockType;
        this.XH = nArray;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(27);
        this.writeH(this.aju ? 1 : 0);
        this.writeH(this._size);
        for (ItemInstance itemInstance : this.ahC) {
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

