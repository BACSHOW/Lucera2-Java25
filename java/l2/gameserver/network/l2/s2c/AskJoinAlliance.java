/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AskJoinAlliance
extends L2GameServerPacket {
    private String adJ;
    private String adK;
    private int adL;

    public AskJoinAlliance(int n, String string, String string2) {
        this.adJ = string;
        this.adK = string2;
        this.adL = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(168);
        this.writeD(this.adL);
        this.writeS(this.adJ);
        this.writeS("");
        this.writeS(this.adK);
    }
}

