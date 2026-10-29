/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExUISetting;

public class RequestSaveKeyMapping
extends L2GameClientPacket {
    private byte[] _data;

    @Override
    protected void readImpl() {
        int n = this.readD();
        if (n > this._buf.remaining() || n > Short.MAX_VALUE || n < 0) {
            this._data = null;
            return;
        }
        this._data = new byte[n];
        this.readB(this._data);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this._data == null) {
            return;
        }
        player.setKeyBindings(this._data);
        player.sendPacket((IStaticPacket)new ExUISetting(player));
    }
}

