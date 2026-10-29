/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPutItemResultForVariationMake
extends L2GameServerPacket {
    public static final ExPutItemResultForVariationMake FAIL_PACKET = new ExPutItemResultForVariationMake(0, false);
    private int _itemObjId;
    private int pv;
    private int oJ;

    public ExPutItemResultForVariationMake(int n, boolean bl) {
        this._itemObjId = n;
        this.pv = 0;
        this.oJ = bl ? 1 : 0;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(82);
        this.writeD(this._itemObjId);
        this.writeD(this.pv);
        this.writeD(this.oJ);
    }
}

