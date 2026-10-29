/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameRemovePlayer
extends L2GameServerPacket {
    private int CS;
    private boolean _isRedTeam;

    public ExCubeGameRemovePlayer(Player player, boolean bl) {
        this.CS = player.getObjectId();
        this._isRedTeam = bl;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(151);
        this.writeD(2);
        this.writeD(-1);
        this.writeD(this._isRedTeam ? 1 : 0);
        this.writeD(this.CS);
    }
}

