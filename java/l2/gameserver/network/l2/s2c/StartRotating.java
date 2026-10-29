/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class StartRotating
extends L2GameServerPacket {
    private int CT;
    private int aaL;
    private int Sx;
    private int GK;

    public StartRotating(Creature creature, int n, int n2, int n3) {
        this.CT = creature.getObjectId();
        this.aaL = n;
        this.Sx = n2;
        this.GK = n3;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(98);
        this.writeD(this.CT);
        this.writeD(this.aaL);
        this.writeD(this.Sx);
        this.writeD(this.GK);
    }
}

