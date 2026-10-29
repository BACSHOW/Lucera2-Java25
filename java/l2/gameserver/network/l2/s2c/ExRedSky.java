/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExRedSky
extends L2GameServerPacket {
    private int _duration;

    public ExRedSky(int n) {
        this._duration = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(64);
        this.writeD(this._duration);
    }
}

