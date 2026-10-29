/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class ValidateLocation
extends L2GameServerPacket {
    private int aix;
    private Location cH;

    public ValidateLocation(Creature creature) {
        this.aix = creature.getObjectId();
        this.cH = creature.getLoc();
    }

    @Override
    protected final void writeImpl() {
        if (this.getClient() != null && this.getClient().getActiveChar() != null && this.getClient().getActiveChar().getObjectId() == this.aix) {
            l2.gameserver.utils.MovementTelemetryLogger.logS2C(this.getClient().getActiveChar(), "*** SNAP CORRECTION (ValidateLocation) ***", String.format("ForcedLoc=(%d,%d,%d, h=%d)", this.cH.x, this.cH.y, this.cH.z + Config.CLIENT_Z_SHIFT, this.cH.h));
        }
        this.writeC(97);
        this.writeD(this.aix);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z + Config.CLIENT_Z_SHIFT);
        this.writeD(this.cH.h);
    }
}

