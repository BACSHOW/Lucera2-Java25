/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class GetOffVehicle
extends L2GameServerPacket {
    private int EQ;
    private int ach;
    private Location cH;

    public GetOffVehicle(Player player, Boat boat, Location location) {
        this.EQ = player.getObjectId();
        this.ach = boat.getObjectId();
        this.cH = location;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(93);
        this.writeD(this.EQ);
        this.writeD(this.ach);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
    }
}

