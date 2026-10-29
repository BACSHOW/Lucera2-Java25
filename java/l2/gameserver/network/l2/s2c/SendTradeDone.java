/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SendTradeDone
extends L2GameServerPacket {
    public static final L2GameServerPacket SUCCESS = new SendTradeDone(1);
    public static final L2GameServerPacket FAIL = new SendTradeDone(0);
    private int py;

    private SendTradeDone(int n) {
        this.py = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(34);
        this.writeD(this.py);
    }
}

