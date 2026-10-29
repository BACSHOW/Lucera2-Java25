/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class Ride
extends L2GameServerPacket {
    private int alX;
    private int _id;
    private int alY;
    private Location cH;

    public Ride(Player player) {
        this._id = player.getObjectId();
        this.alX = player.getMountType();
        this.alY = player.getMountNpcId() + 1000000;
        this.cH = player.getLoc();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(134);
        this.writeD(this._id);
        this.writeD(this.alX == 0 ? 0 : 1);
        this.writeD(this.alX);
        this.writeD(this.alY);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
    }
}

