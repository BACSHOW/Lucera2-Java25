/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class StopMoveToLocationInVehicle
extends L2GameServerPacket {
    private int ach;
    private int EQ;
    private int Ef;
    private Location cH;

    public StopMoveToLocationInVehicle(Player player) {
        this.ach = player.getBoat().getObjectId();
        this.EQ = player.getObjectId();
        this.cH = player.getInBoatPosition();
        this.Ef = player.getHeading();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(114);
        this.writeD(this.EQ);
        this.writeD(this.ach);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeD(this.Ef);
    }
}

