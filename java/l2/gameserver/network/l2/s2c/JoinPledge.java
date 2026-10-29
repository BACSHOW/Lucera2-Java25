/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class JoinPledge
extends L2GameServerPacket {
    private int ajv;

    public JoinPledge(int n) {
        this.ajv = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(51);
        this.writeD(this.ajv);
    }
}

