/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class CameraMode
extends L2GameServerPacket {
    int _mode;

    public CameraMode(int n) {
        this._mode = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(241);
        this.writeD(this._mode);
    }
}

