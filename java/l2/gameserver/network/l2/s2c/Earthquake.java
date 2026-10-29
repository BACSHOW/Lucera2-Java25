/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class Earthquake
extends L2GameServerPacket {
    private Location cH;
    private int afE;
    private int _duration;

    public Earthquake(Location location, int n, int n2) {
        this.cH = location;
        this.afE = n;
        this._duration = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(196);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeD(this.afE);
        this.writeD(this._duration);
        this.writeD(0);
    }
}

