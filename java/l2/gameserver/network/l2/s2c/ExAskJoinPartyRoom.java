/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExAskJoinPartyRoom
extends L2GameServerPacket {
    private String agb;
    private String agc;

    public ExAskJoinPartyRoom(String string, String string2) {
        this.agb = string;
        this.agc = string2;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(52);
        this.writeS(this.agb);
    }
}

