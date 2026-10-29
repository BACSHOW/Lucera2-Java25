/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.c2s;

import l2.authserver.GameServerManager;
import l2.authserver.accounts.Account;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ProxyServer;
import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.SessionKey;
import l2.authserver.network.l2.c2s.L2LoginClientPacket;
import l2.authserver.network.l2.s2c.LoginFail;
import l2.authserver.network.l2.s2c.PlayOk;

public class RequestServerLogin
extends L2LoginClientPacket {
    private int pt;
    private int pu;
    private int pv;

    @Override
    protected void readImpl() {
        this.pt = this.readD();
        this.pu = this.readD();
        this.pv = this.readC();
    }

    @Override
    protected void runImpl() {
        ProxyServer proxyServer;
        L2LoginClient l2LoginClient = (L2LoginClient)this.getClient();
        SessionKey sessionKey = l2LoginClient.getSessionKey();
        if (sessionKey == null || !sessionKey.checkLoginPair(this.pt, this.pu)) {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }
        Account account = l2LoginClient.getAccount();
        GameServer gameServer = GameServerManager.getInstance().getGameServerById(this.pv);
        if (gameServer == null && (proxyServer = GameServerManager.getInstance().getProxyServerById(this.pv)) != null) {
            gameServer = GameServerManager.getInstance().getGameServerById(proxyServer.getOrigServerId());
        }
        if (gameServer == null || !gameServer.isAuthed() || gameServer.getOnline() >= gameServer.getMaxPlayers() && account.getAccessLevel() < 50) {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }
        if (gameServer.isGmOnly() && account.getAccessLevel() < 100) {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_SERVER_MAINTENANCE);
            return;
        }
        account.setLastServer(this.pv);
        account.update();
        l2LoginClient.close(new PlayOk(sessionKey));
    }
}

