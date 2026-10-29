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

public class RequestBBSwrite
extends L2GameClientPacket {
    private String abd;
    private String abe;
    private String abf;
    private String abg;
    private String abh;
    private String abi;

    @Override
    public void readImpl() {
        this.abd = this.readS();
        this.abe = this.readS();
        this.abf = this.readS();
        this.abg = this.readS();
        this.abh = this.readS();
        this.abi = this.readS();
    }

    @Override
    public void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        ICommunityBoardHandler iCommunityBoardHandler = CommunityBoardManager.getInstance().getCommunityHandler(this.abd, player);
        if (iCommunityBoardHandler != null) {
            if (!Config.COMMUNITYBOARD_ENABLED) {
                player.sendPacket((IStaticPacket)new SystemMessage(938));
            } else {
                iCommunityBoardHandler.onWriteCommand(player, this.abd, this.abe, this.abf, this.abg, this.abh, this.abi);
            }
        }
    }
}

