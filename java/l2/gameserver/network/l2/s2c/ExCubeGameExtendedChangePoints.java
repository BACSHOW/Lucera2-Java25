/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameExtendedChangePoints
extends L2GameServerPacket {
    private int _timeLeft;
    private int _bluePoints;
    private int _redPoints;
    private boolean _isRedTeam;
    private int CS;
    private int agx;

    public ExCubeGameExtendedChangePoints(int n, int n2, int n3, boolean bl, Player player, int n4) {
        this._timeLeft = n;
        this._bluePoints = n2;
        this._redPoints = n3;
        this._isRedTeam = bl;
        this.CS = player.getObjectId();
        this.agx = n4;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(152);
        this.writeD(0);
        this.writeD(this._timeLeft);
        this.writeD(this._bluePoints);
        this.writeD(this._redPoints);
        this.writeD(this._isRedTeam ? 1 : 0);
        this.writeD(this.CS);
        this.writeD(this.agx);
    }
}

