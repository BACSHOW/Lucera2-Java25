/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExRotation
extends L2GameServerPacket {
    private int agM;
    private int aaL;

    public ExRotation(int n, int n2) {
        this.agM = n;
        this.aaL = n2;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(193);
        this.writeD(this.agM);
        this.writeD(this.aaL);
    }
}

