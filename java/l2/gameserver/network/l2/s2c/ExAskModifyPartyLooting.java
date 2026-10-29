/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExAskModifyPartyLooting
extends L2GameServerPacket {
    private String _requestor;
    private int _mode;

    public ExAskModifyPartyLooting(String string, int n) {
        this._requestor = string;
        this._mode = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(191);
        this.writeS(this._requestor);
        this.writeD(this._mode);
    }
}

