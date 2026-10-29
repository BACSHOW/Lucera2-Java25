/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExShowRefineryInterface
extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC = new ExShowRefineryInterface();

    @Override
    protected final void writeImpl() {
        this.writeEx(80);
    }
}

