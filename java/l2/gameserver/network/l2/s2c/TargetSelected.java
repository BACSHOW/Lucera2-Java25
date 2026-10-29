/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class TargetSelected
extends L2GameServerPacket {
    private int CS;
    private int _targetId;
    private Location cH;

    public TargetSelected(int n, int n2, Location location) {
        this.CS = n;
        this._targetId = n2;
        this.cH = location;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(41);
        this.writeD(this.CS);
        this.writeD(this._targetId);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
    }
}

