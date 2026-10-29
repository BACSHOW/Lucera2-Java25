/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.as2gs;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.ReceivablePacket;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ServerClose;

public class KickPlayer
extends ReceivablePacket {
    String account;

    @Override
    public void readImpl() {
        this.account = this.readS();
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = AuthServerCommunication.getInstance().removeWaitingClient(this.account);
        if (gameClient == null) {
            gameClient = AuthServerCommunication.getInstance().removeAuthedClient(this.account);
        }
        if (gameClient == null) {
            return;
        }
        Player player = gameClient.getActiveChar();
        if (player != null) {
            player.sendPacket((IStaticPacket)Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
            player.kick();
        } else {
            gameClient.close(ServerClose.STATIC);
        }
    }
}

