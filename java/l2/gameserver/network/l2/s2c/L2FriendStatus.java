/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class L2FriendStatus
extends L2GameServerPacket {
    private String agb;
    private boolean ajA;

    public L2FriendStatus(Player player, boolean bl) {
        this.ajA = bl;
        this.agb = player.getName();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(252);
        this.writeD(this.ajA ? 1 : 0);
        this.writeS(this.agb);
        this.writeD(0);
    }
}

