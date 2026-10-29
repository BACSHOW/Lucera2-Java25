/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Shutdown;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.SessionKey;
import l2.gameserver.network.authcomm.gs2as.PlayerAuthRequest;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.LoginFail;
import l2.gameserver.network.l2.s2c.ServerClose;

public class AuthLogin
extends L2GameClientPacket {
    private String aaA;
    private int aaB;
    private int aaC;
    private int aaD;
    private int aaE;
    private int aaF;

    @Override
    protected void readImpl() {
        this.aaA = this.readS(32).toLowerCase();
        this.aaC = this.readD();
        this.aaB = this.readD();
        this.aaD = this.readD();
        this.aaE = this.readD();
        if (this.getAvaliableBytes() >= 4) {
            this.aaF = this.readD();
        }
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        SessionKey sessionKey = new SessionKey(this.aaD, this.aaE, this.aaB, this.aaC);
        gameClient.setSessionId(sessionKey);
        gameClient.setLoginName(this.aaA);
        if (gameClient.getRevision() == 0 || Shutdown.getInstance().getMode() != -1 && Shutdown.getInstance().getSeconds() <= 15) {
            gameClient.closeNow(false);
        } else {
            if (AuthServerCommunication.getInstance().isShutdown()) {
                gameClient.close(new LoginFail(LoginFail.SYSTEM_ERROR_LOGIN_LATER));
                return;
            }
            GameClient gameClient2 = AuthServerCommunication.getInstance().addWaitingClient(gameClient);
            if (gameClient2 != null) {
                gameClient2.close(ServerClose.STATIC);
            }
            AuthServerCommunication.getInstance().sendPacket(new PlayerAuthRequest(gameClient));
        }
    }
}

