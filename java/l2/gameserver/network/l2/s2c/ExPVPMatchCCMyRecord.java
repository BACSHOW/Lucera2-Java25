/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPVPMatchCCMyRecord
extends L2GameServerPacket {
    private int _points;

    public ExPVPMatchCCMyRecord(int n) {
        this._points = n;
    }

    @Override
    public void writeImpl() {
        this.writeEx(138);
        this.writeD(this._points);
    }
}

