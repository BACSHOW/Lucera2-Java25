/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExAskJoinMPCC
extends L2GameServerPacket {
    private String adJ;

    public ExAskJoinMPCC(String string) {
        this.adJ = string;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(39);
        this.writeS(this.adJ);
    }
}

