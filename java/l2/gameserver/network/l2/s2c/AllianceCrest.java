/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AllianceCrest
extends L2GameServerPacket {
    private int Yv;
    private byte[] _data;

    public AllianceCrest(int n, byte[] byArray) {
        this.Yv = n;
        this._data = byArray;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(174);
        this.writeD(this.Yv);
        this.writeD(this._data.length);
        this.writeB(this._data);
    }
}

