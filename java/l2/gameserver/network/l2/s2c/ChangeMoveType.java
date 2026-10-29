/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ChangeMoveType
extends L2GameServerPacket {
    public static int WALK = 0;
    public static int RUN = 1;
    private int aed;
    private boolean DZ;

    public ChangeMoveType(Creature creature) {
        this.aed = creature.getObjectId();
        this.DZ = creature.isRunning();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(46);
        this.writeD(this.aed);
        this.writeD(this.DZ ? 1 : 0);
        this.writeD(0);
    }
}

