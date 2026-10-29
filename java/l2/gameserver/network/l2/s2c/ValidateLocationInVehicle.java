/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class ValidateLocationInVehicle
extends L2GameServerPacket {
    private int EQ;
    private int ach;
    private Location cH;

    public ValidateLocationInVehicle(Player player) {
        this.EQ = player.getObjectId();
        this.ach = player.getBoat().getObjectId();
        this.cH = player.getInBoatPosition();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(115);
        this.writeD(this.EQ);
        this.writeD(this.ach);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeD(this.cH.h);
    }
}

