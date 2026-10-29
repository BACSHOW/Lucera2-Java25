/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class FlyToLocation
extends L2GameServerPacket {
    private int aix;
    private final FlyType aiy;
    private Location cH;
    private Location aiz;

    public FlyToLocation(Creature creature, Location location, FlyType flyType) {
        this.aiz = location;
        this.aiy = flyType;
        this.aix = creature.getObjectId();
        this.cH = creature.getLoc();
    }

    @Override
    protected void writeImpl() {
        this.writeC(212);
        this.writeD(this.aix);
        this.writeD(this.aiz.x);
        this.writeD(this.aiz.y);
        this.writeD(this.aiz.z);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeD(this.aiy.ordinal());
    }

    public static enum FlyType {
        THROW_UP,
        THROW_HORIZONTAL,
        DUMMY,
        CHARGE,
        NONE;

    }
}

