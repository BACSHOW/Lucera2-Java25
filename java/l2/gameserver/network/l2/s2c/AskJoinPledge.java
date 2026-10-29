/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AskJoinPledge
extends L2GameServerPacket {
    private int adL;
    private String acU;

    public AskJoinPledge(int n, String string) {
        this.adL = n;
        this.acU = string;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(50);
        this.writeD(this.adL);
        this.writeS(this.acU);
    }
}

