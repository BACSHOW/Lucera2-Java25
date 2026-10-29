/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class EventTrigger
extends L2GameServerPacket {
    private int afQ;
    private boolean _active;

    public EventTrigger(int n, boolean bl) {
        this.afQ = n;
        this._active = bl;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(207);
        this.writeD(this.afQ);
        this.writeC(this._active ? 1 : 0);
    }
}

