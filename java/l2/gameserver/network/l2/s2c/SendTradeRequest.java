/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SendTradeRequest
extends L2GameServerPacket {
    private int amj;

    public SendTradeRequest(int n) {
        this.amj = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(94);
        this.writeD(this.amj);
    }
}

