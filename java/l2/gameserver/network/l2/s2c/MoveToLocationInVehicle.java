/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class MoveToLocationInVehicle
extends L2GameServerPacket {
    private int EQ;
    private int ach;
    private Location ajN;
    private Location _destination;

    public MoveToLocationInVehicle(Player player, Boat boat, Location location, Location location2) {
        this.EQ = player.getObjectId();
        this.ach = boat.getObjectId();
        this.ajN = location;
        this._destination = location2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(113);
        this.writeD(this.EQ);
        this.writeD(this.ach);
        this.writeD(this._destination.x);
        this.writeD(this._destination.y);
        this.writeD(this._destination.z);
        this.writeD(this.ajN.x);
        this.writeD(this.ajN.y);
        this.writeD(this.ajN.z);
    }
}

