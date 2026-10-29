/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestPrivateStoreQuitSell
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (!player.isInStoreMode() || player.getPrivateStoreType() != 1 && player.getPrivateStoreType() != 8) {
            player.sendActionFailed();
            return;
        }
        player.setPrivateStoreType(0);
    }
}

