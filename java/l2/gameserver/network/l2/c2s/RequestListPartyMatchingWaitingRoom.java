/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExListPartyMatchingWaitingRoom;

public class RequestListPartyMatchingWaitingRoom
extends L2GameClientPacket {
    private int p;
    private int s;
    private int _page;
    private int[] acd;

    @Override
    protected void readImpl() {
        this._page = this.readD();
        this.p = this.readD();
        this.s = this.readD();
        int n = this.readD();
        if (n > 127 || n < 0) {
            n = 0;
        }
        this.acd = new int[n];
        for (int i = 0; i < n; ++i) {
            this.acd[i] = this.readD();
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        player.sendPacket((IStaticPacket)new ExListPartyMatchingWaitingRoom(player, this.p, this.s, this._page, this.acd));
    }
}

