/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeCrest
extends L2GameServerPacket {
    private int Yv;
    private int akY;
    private byte[] _data;

    public PledgeCrest(int n, byte[] byArray) {
        this.Yv = n;
        this._data = byArray;
        this.akY = this._data.length;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(108);
        this.writeD(this.Yv);
        this.writeD(this.akY);
        this.writeB(this._data);
    }
}

