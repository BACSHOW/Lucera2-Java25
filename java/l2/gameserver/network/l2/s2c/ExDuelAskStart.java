/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExDuelAskStart
extends L2GameServerPacket {
    String _requestor;
    int _isPartyDuel;

    public ExDuelAskStart(String string, int n) {
        this._requestor = string;
        this._isPartyDuel = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(75);
        this.writeS(this._requestor);
        this.writeD(this._isPartyDuel);
    }
}

