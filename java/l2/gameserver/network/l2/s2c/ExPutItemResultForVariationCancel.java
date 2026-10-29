/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPutItemResultForVariationCancel
extends L2GameServerPacket {
    private int ahy;
    private int _itemId;
    private int ahz;
    private int ahA;
    private long _price;
    private boolean ahB;

    public ExPutItemResultForVariationCancel(ItemInstance itemInstance, long l, boolean bl) {
        this.ahy = itemInstance.getObjectId();
        this._itemId = itemInstance.getItemId();
        this.ahz = itemInstance.getVariationStat1();
        this.ahA = itemInstance.getVariationStat2();
        this._price = l;
        this.ahB = bl;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(86);
        this.writeD(this.ahy);
        this.writeD(this._itemId);
        this.writeD(this.ahz);
        this.writeD(this.ahA);
        this.writeD((int)this._price);
        this.writeD(0);
        this.writeD(this.ahB ? 1 : 0);
    }
}

