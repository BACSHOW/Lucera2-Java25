/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExShowQuestMark
extends L2GameServerPacket {
    private int _questId;

    public ExShowQuestMark(int n) {
        this._questId = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(26);
        this.writeD(this._questId);
    }
}

