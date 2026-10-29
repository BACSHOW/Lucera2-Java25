/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class RadarControl
extends L2GameServerPacket {
    private int _x;
    private int _y;
    private int Di;
    private int _type;
    private int alD;

    public RadarControl(int n, int n2, Location location) {
        this(n, n2, location.x, location.y, location.z);
    }

    public RadarControl(int n, int n2, int n3, int n4, int n5) {
        this.alD = n;
        this._type = n2;
        this._x = n3;
        this._y = n4;
        this.Di = n5;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(235);
        this.writeD(this.alD);
        this.writeD(this._type);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
    }
}

