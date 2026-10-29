/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPVPMatchUserDie
extends L2GameServerPacket {
    private int ahs;
    private int aht;

    public ExPVPMatchUserDie(int n, int n2) {
        this.ahs = n;
        this.aht = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(127);
        this.writeD(this.ahs);
        this.writeD(this.aht);
    }
}

