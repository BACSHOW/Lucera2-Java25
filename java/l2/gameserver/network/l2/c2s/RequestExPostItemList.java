/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExReplyPostItemList;

public class RequestExPostItemList
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
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
        }
        if (!Config.ALLOW_MAIL) {
            player.sendMessage(new CustomMessage("mail.Disabled", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        player.sendPacket((IStaticPacket)new ExReplyPostItemList(player));
    }
}

