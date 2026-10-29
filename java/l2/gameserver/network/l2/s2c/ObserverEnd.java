/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class ObserverEnd
extends L2GameServerPacket {
    private Location cH;

    public ObserverEnd(Location location) {
        this.cH = location;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(224);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
    }
}

