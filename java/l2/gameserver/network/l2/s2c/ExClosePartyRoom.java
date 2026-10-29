/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExClosePartyRoom
extends L2GameServerPacket {
    public static L2GameServerPacket STATIC = new ExClosePartyRoom();

    @Override
    protected void writeImpl() {
        this.writeEx(9);
    }
}

