/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class StartPledgeWar
extends L2GameServerPacket {
    private String acU;
    private String amO;

    public StartPledgeWar(String string, String string2) {
        this.acU = string;
        this.amO = string2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(101);
        this.writeS(this.amO);
        this.writeS(this.acU);
    }
}

