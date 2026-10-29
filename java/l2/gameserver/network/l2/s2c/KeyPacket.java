/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class KeyPacket
extends L2GameServerPacket {
    private byte[] ajw;

    public KeyPacket(Object object) {
        this.ajw = (byte[])object;
    }

    @Override
    public void writeImpl() {
        this.writeC(0);
        if (this.ajw == null || this.ajw.length == 0) {
            this.writeC(0);
            return;
        }
        this.writeC(1);
        this.writeB(this.ajw);
        this.writeD(1);
        this.writeQ(0L);
        this.writeQ(0L);
    }
}

