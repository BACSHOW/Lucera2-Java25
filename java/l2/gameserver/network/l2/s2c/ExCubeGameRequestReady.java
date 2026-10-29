/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExCubeGameRequestReady
extends L2GameServerPacket {
    @Override
    protected void writeImpl() {
        this.writeEx(151);
        this.writeD(4);
    }
}

