/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SurrenderPledgeWar
extends L2GameServerPacket {
    private String acU;
    private String amO;

    public SurrenderPledgeWar(String string, String string2) {
        this.acU = string;
        this.amO = string2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(105);
        this.writeS(this.acU);
        this.writeS(this.amO);
    }
}

