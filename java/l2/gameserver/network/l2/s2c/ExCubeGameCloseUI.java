/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameCloseUI
extends L2GameServerPacket {
    int _seconds;

    @Override
    protected void writeImpl() {
        this.writeEx(151);
        this.writeD(-1);
    }
}

