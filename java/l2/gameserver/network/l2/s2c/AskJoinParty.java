/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AskJoinParty
extends L2GameServerPacket {
    private String adJ;
    private int Gq;

    public AskJoinParty(String string, int n) {
        this.adJ = string;
        this.Gq = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(57);
        this.writeS(this.adJ);
        this.writeD(this.Gq);
    }
}

