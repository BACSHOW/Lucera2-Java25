/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class MyTargetSelected
extends L2GameServerPacket {
    private int CS;
    private int ahX;

    public MyTargetSelected(int n, int n2) {
        this.CS = n;
        this.ahX = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(166);
        this.writeD(this.CS);
        this.writeH(this.ahX);
        this.writeD(0);
    }
}

