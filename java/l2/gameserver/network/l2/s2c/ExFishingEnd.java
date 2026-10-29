/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExFishingEnd
extends L2GameServerPacket {
    private int CT;
    private boolean agF;

    public ExFishingEnd(Player player, boolean bl) {
        this.CT = player.getObjectId();
        this.agF = bl;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(20);
        this.writeD(this.CT);
        this.writeC(this.agF ? 1 : 0);
    }
}

