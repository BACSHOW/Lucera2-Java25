/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameEnd
extends L2GameServerPacket {
    boolean _isRedTeamWin;

    public ExCubeGameEnd(boolean bl) {
        this._isRedTeamWin = bl;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(152);
        this.writeD(1);
        this.writeD(this._isRedTeamWin ? 1 : 0);
    }
}

