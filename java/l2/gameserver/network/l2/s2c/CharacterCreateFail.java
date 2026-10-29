/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class CharacterCreateFail
extends L2GameServerPacket {
    public static final L2GameServerPacket REASON_CREATION_FAILED = new CharacterCreateFail(0);
    public static final L2GameServerPacket REASON_TOO_MANY_CHARACTERS = new CharacterCreateFail(1);
    public static final L2GameServerPacket REASON_NAME_ALREADY_EXISTS = new CharacterCreateFail(2);
    public static final L2GameServerPacket REASON_16_ENG_CHARS = new CharacterCreateFail(3);
    private int _error;

    private CharacterCreateFail(int n) {
        this._error = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(26);
        this.writeD(this._error);
    }
}

