/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestSaveInventoryOrder
extends L2GameClientPacket {
    int[][] _items;

    @Override
    protected void readImpl() {
        int n = this.readD();
        if (n > 125) {
            n = 125;
        }
        if (n * 8 > this._buf.remaining() || n < 1) {
            this._items = null;
            return;
        }
        this._items = new int[n][2];
        for (int i = 0; i < n; ++i) {
            this._items[i][0] = this.readD();
            this._items[i][1] = this.readD();
        }
    }

    @Override
    protected void runImpl() {
        if (this._items == null) {
            return;
        }
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        player.getInventory().sort(this._items);
    }
}

