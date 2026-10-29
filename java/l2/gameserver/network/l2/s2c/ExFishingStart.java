/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class ExFishingStart
extends L2GameServerPacket {
    private int agM;
    private Location cH;
    private int agN;
    private boolean agO;

    public ExFishingStart(Creature creature, int n, Location location, boolean bl) {
        this.agM = creature.getObjectId();
        this.agN = n;
        this.cH = location;
        this.agO = bl;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(19);
        this.writeD(this.agM);
        this.writeD(this.agN);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
        this.writeC(this.agO ? 1 : 0);
        this.writeC(1);
    }
}

