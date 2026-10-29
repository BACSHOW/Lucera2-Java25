/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class FinishRotating
extends L2GameServerPacket {
    private int CT;
    private int aaL;
    private int GK;

    public FinishRotating(Creature creature, int n, int n2) {
        this.CT = creature.getObjectId();
        this.aaL = n;
        this.GK = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(99);
        this.writeD(this.CT);
        this.writeD(this.aaL);
        this.writeD(this.GK);
        this.writeD(0);
    }
}

