/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExChangeClientEffectInfo
extends L2GameServerPacket {
    private int _state;

    public ExChangeClientEffectInfo(int n) {
        this._state = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(193);
        this.writeD(0);
        this.writeD(this._state);
    }
}

