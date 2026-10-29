/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class FriendAddRequest
extends L2GameServerPacket {
    private String adJ;

    public FriendAddRequest(String string) {
        this.adJ = string;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(125);
        this.writeS(this.adJ);
        this.writeD(0);
    }
}

