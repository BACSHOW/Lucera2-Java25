/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListSell;
import l2.gameserver.utils.TradeHelper;

public class RequestPrivateStoreSell
extends L2GameClientPacket {
    @Override
    protected void readImpl() throws Exception {
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.getSittingTask()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.setPrivateStoreType(0);
        } else if (!TradeHelper.checksIfCanOpenStore(player, 1)) {
            player.sendActionFailed();
            return;
        }
        player.sendPacket((IStaticPacket)new PrivateStoreManageListSell(player, false));
    }
}

