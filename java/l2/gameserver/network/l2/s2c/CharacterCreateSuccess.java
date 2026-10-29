/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class CharacterCreateSuccess
extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC = new CharacterCreateSuccess();

    @Override
    protected final void writeImpl() {
        this.writeC(25);
        this.writeD(1);
    }
}

