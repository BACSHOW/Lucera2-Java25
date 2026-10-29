/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestExBuySellUIClose
extends L2GameClientPacket {
    @Override
    protected void runImpl() {
    }

    @Override
    protected void readImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        player.setBuyListId(0);
        player.sendItemList(true);
    }
}

