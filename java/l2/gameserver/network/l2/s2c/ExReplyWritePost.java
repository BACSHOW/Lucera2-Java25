/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExReplyWritePost
extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC_TRUE = new ExReplyWritePost(1);
    public static final L2GameServerPacket STATIC_FALSE = new ExReplyWritePost(0);
    private int ahM;

    public ExReplyWritePost(int n) {
        this.ahM = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(180);
        this.writeD(this.ahM);
    }
}

