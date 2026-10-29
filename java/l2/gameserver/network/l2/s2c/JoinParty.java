/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class JoinParty
extends L2GameServerPacket {
    public static final L2GameServerPacket SUCCESS = new JoinParty(1);
    public static final L2GameServerPacket FAIL = new JoinParty(0);
    private int py;

    public JoinParty(int n) {
        this.py = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(58);
        this.writeD(this.py);
    }
}

