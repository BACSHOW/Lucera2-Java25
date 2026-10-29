/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class L2FriendSay
extends L2GameServerPacket {
    private String Kv;
    private String ajz;
    private String acK;

    public L2FriendSay(String string, String string2, String string3) {
        this.Kv = string;
        this.ajz = string2;
        this.acK = string3;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(253);
        this.writeD(0);
        this.writeS(this.ajz);
        this.writeS(this.Kv);
        this.writeS(this.acK);
    }
}

