/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExAutoSoulShot
extends L2GameServerPacket {
    private final int _itemId;
    private final boolean abc;

    public ExAutoSoulShot(int n, boolean bl) {
        this._itemId = n;
        this.abc = bl;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(18);
        this.writeD(this._itemId);
        this.writeD(this.abc ? 1 : 0);
    }
}

