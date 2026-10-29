/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class VehicleDeparture
extends L2GameServerPacket {
    private int Qj;
    private int Qk;
    private int anD;
    private Location cH;

    public VehicleDeparture(Boat boat) {
        this.anD = boat.getObjectId();
        this.Qj = boat.getMoveSpeed();
        this.Qk = boat.getRotationSpeed();
        this.cH = boat.getDestination();
        if (this.cH == null) {
            this.cH = boat.getReturnLoc();
        }
    }

    public VehicleDeparture(Boat boat, Location location) {
        this.anD = boat.getObjectId();
        this.Qj = boat.getMoveSpeed();
        this.Qk = boat.getRotationSpeed();
        this.cH = location;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(90);
        this.writeD(this.anD);
        this.writeD(this.Qj);
        this.writeD(this.Qk);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
    }
}

