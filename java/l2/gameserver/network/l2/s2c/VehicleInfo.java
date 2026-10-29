/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class VehicleInfo
extends L2GameServerPacket {
    private int ach;
    private Location cH;

    public VehicleInfo(Boat boat) {
        this.ach = boat.getObjectId();
        this.cH = boat.getLoc();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(89);
        this.writeD(this.ach);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeD(this.cH.h);
    }
}

