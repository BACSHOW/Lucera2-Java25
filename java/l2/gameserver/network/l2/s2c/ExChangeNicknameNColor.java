/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExChangeNicknameNColor
extends L2GameServerPacket {
    private int _itemObjId;

    public ExChangeNicknameNColor(int n) {
        this._itemObjId = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(131);
        this.writeD(this._itemObjId);
    }
}

