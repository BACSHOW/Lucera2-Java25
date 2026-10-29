/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameChangeTimeToStart
extends L2GameServerPacket {
    int _seconds;

    public ExCubeGameChangeTimeToStart(int n) {
        this._seconds = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(151);
        this.writeD(3);
        this.writeD(this._seconds);
    }
}

