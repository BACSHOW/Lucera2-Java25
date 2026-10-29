/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeShowMemberListDelete
extends L2GameServerPacket {
    private String alv;

    public PledgeShowMemberListDelete(String string) {
        this.alv = string;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(86);
        this.writeS(this.alv);
    }
}

