/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class ExJumpToLocation
extends L2GameServerPacket {
    private int CS;
    private Location afd;
    private Location _destination;

    public ExJumpToLocation(int n, Location location, Location location2) {
        this.CS = n;
        this.afd = location;
        this._destination = location2;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(136);
        this.writeD(this.CS);
        this.writeD(this._destination.x);
        this.writeD(this._destination.y);
        this.writeD(this._destination.z);
        this.writeD(this.afd.x);
        this.writeD(this.afd.y);
        this.writeD(this.afd.z);
    }
}

