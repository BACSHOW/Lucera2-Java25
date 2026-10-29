/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPledgeCrestLarge
extends L2GameServerPacket {
    private int Yv;
    private byte[] _data;

    public ExPledgeCrestLarge(int n, byte[] byArray) {
        this.Yv = n;
        this._data = byArray;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(40);
        this.writeD(0);
        this.writeD(this.Yv);
        this.writeD(this._data.length);
        this.writeB(this._data);
    }
}

