/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PetDelete
extends L2GameServerPacket {
    private int akx;
    private int aky;

    public PetDelete(int n, int n2) {
        this.akx = n;
        this.aky = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(182);
        this.writeD(this.akx);
        this.writeD(this.aky);
    }
}

