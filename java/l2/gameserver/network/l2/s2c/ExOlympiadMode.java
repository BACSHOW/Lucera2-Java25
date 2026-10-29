/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExOlympiadMode
extends L2GameServerPacket {
    private int _mode;

    public ExOlympiadMode(int n) {
        this._mode = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(43);
        this.writeC(this._mode);
    }
}

