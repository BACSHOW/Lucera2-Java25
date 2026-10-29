/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class Appearing
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
        if (player.isLogoutStarted()) {
            player.sendActionFailed();
            return;
        }
        if (player.getObserverMode() == 1) {
            player.appearObserverMode();
            return;
        }
        if (player.getObserverMode() == 2) {
            player.returnFromObserverMode();
            return;
        }
        if (!player.isTeleporting()) {
            player.sendActionFailed();
            return;
        }
        player.onTeleported();
    }
}

