/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPlayScene
extends L2GameServerPacket {
    public static final ExPlayScene STATIC = new ExPlayScene();

    @Override
    protected void writeImpl() {
        this.writeEx(91);
        this.writeD(0);
    }
}

