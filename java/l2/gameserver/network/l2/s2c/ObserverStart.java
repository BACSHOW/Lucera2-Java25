/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class ObserverStart
extends L2GameServerPacket {
    private Location cH;

    public ObserverStart(Location location) {
        this.cH = location;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(223);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeC(0);
        this.writeC(192);
        this.writeC(0);
    }
}

