/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameTeamList
extends L2GameServerPacket {
    List<Player> _bluePlayers;
    List<Player> _redPlayers;
    int _roomNumber;

    public ExCubeGameTeamList(List<Player> list, List<Player> list2, int n) {
        this._redPlayers = list;
        this._bluePlayers = list2;
        this._roomNumber = n - 1;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(151);
        this.writeD(0);
        this.writeD(this._roomNumber);
        this.writeD(-1);
        this.writeD(this._bluePlayers.size());
        for (Player player : this._bluePlayers) {
            this.writeD(player.getObjectId());
            this.writeS(player.getName());
        }
        this.writeD(this._redPlayers.size());
        for (Player player : this._redPlayers) {
            this.writeD(player.getObjectId());
            this.writeS(player.getName());
        }
    }
}

