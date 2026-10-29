/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameChangePoints
extends L2GameServerPacket {
    int _timeLeft;
    int _bluePoints;
    int _redPoints;

    public ExCubeGameChangePoints(int n, int n2, int n3) {
        this._timeLeft = n;
        this._bluePoints = n2;
        this._redPoints = n3;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(152);
        this.writeD(2);
        this.writeD(this._timeLeft);
        this.writeD(this._bluePoints);
        this.writeD(this._redPoints);
    }
}

