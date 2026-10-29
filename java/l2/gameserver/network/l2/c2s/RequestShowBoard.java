/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestShowBoard
extends L2GameClientPacket {
    private int aaM;

    @Override
    public void readImpl() {
        this.aaM = this.readD();
    }

    @Override
    public void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (Config.COMMUNITYBOARD_ENABLED) {
            ICommunityBoardHandler iCommunityBoardHandler = CommunityBoardManager.getInstance().getCommunityHandler(Config.BBS_DEFAULT, player);
            if (iCommunityBoardHandler != null) {
                iCommunityBoardHandler.onBypassCommand(player, Config.BBS_DEFAULT);
            }
        } else {
            player.sendPacket((IStaticPacket)new SystemMessage(938));
        }
    }
}

