/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExBR_GamePoint
extends L2GameServerPacket {
    private int CS;
    private long agm;

    public ExBR_GamePoint(Player player) {
        this.CS = player.getObjectId();
        this.agm = player.getPremiumPoints();
    }

    @Override
    protected void writeImpl() {
        this.writeEx(213);
        this.writeD(this.CS);
        this.writeQ(this.agm);
        this.writeD(0);
    }
}

