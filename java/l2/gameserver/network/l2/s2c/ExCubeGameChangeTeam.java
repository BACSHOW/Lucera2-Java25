/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameChangeTeam
extends L2GameServerPacket {
    private int CS;
    private boolean agw;

    public ExCubeGameChangeTeam(Player player, boolean bl) {
        this.CS = player.getObjectId();
        this.agw = bl;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(151);
        this.writeD(5);
        this.writeD(this.CS);
        this.writeD(this.agw ? 1 : 0);
        this.writeD(this.agw ? 0 : 1);
    }
}

