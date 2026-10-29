/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SunSet
extends L2GameServerPacket {
    @Override
    protected final void writeImpl() {
        this.writeC(29);
    }
}

