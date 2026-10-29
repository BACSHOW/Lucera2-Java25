/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.GameServerManager;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;
import l2.authserver.network.gamecomm.as2gs.AuthResponse;
import l2.authserver.network.gamecomm.as2gs.LoginServerFail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthRequest
extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(AuthRequest.class);
    private int oO;
    private int requestId;
    private boolean oP;
    private String oQ;
    private String oR;
    private int oS;
    private int ol;
    private int om;
    private boolean oT;
    private boolean oU;
    private boolean oV;
    private int[] oW;
    private int ok;

    @Override
    protected void readImpl() {
        this.oO = this.readD();
        this.requestId = this.readC();
        this.oP = this.readC() == 1;
        this.ol = this.readD();
        this.om = this.readD();
        this.oT = this.readC() == 1;
        this.oU = this.readC() == 1;
        this.oV = this.readC() == 1;
        this.oQ = this.readS();
        this.oR = this.readS();
        this.oW = new int[this.readH()];
        for (int i = 0; i < this.oW.length; ++i) {
            this.oW[i] = this.readH();
        }
        this.oS = this.readD();
        this.ok = this.readH();
    }

    @Override
    protected void runImpl() {
        _log.info("Trying to register gameserver: " + this.requestId + " [" + this.getGameServer().getConnection().getIpAddress() + "]");
        int n = 0;
        GameServer gameServer = this.getGameServer();
        if (GameServerManager.getInstance().registerGameServer(this.requestId, gameServer)) {
            gameServer.setPorts(this.oW);
            gameServer.setExternalHost(this.oQ);
            gameServer.setInternalHost(this.oR);
            gameServer.setMaxPlayers(this.oS);
            gameServer.setPvp(this.oV);
            gameServer.setServerType(this.ol);
            gameServer.setShowingBrackets(this.oU);
            gameServer.setGmOnly(this.oT);
            gameServer.setAgeLimit(this.om);
            gameServer.setProtocol(this.oO);
            gameServer.setAuthed(true);
            gameServer.getConnection().startPingTask();
            gameServer.setHaProxyPort(this.ok);
        } else if (this.oP) {
            gameServer = this.getGameServer();
            if (GameServerManager.getInstance().registerGameServer(gameServer)) {
                gameServer.setPorts(this.oW);
                gameServer.setExternalHost(this.oQ);
                gameServer.setInternalHost(this.oR);
                gameServer.setMaxPlayers(this.oS);
                gameServer.setPvp(this.oV);
                gameServer.setServerType(this.ol);
                gameServer.setShowingBrackets(this.oU);
                gameServer.setGmOnly(this.oT);
                gameServer.setAgeLimit(this.om);
                gameServer.setProtocol(this.oO);
                gameServer.setAuthed(true);
                gameServer.getConnection().startPingTask();
                gameServer.setHaProxyPort(this.ok);
            } else {
                n = 5;
            }
        } else {
            n = 4;
        }
        if (n != 0) {
            _log.info("Gameserver registration failed.");
            this.sendPacket(new LoginServerFail(n));
            return;
        }
        _log.info("Gameserver registration successful.");
        this.sendPacket(new AuthResponse(gameServer));
    }
}

