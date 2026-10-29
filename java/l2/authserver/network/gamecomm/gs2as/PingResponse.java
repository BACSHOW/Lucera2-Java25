/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingResponse
extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(PingResponse.class);
    private long pd;

    @Override
    protected void readImpl() {
        this.pd = this.readQ();
    }

    @Override
    protected void runImpl() {
        GameServer gameServer = this.getGameServer();
        if (!gameServer.isAuthed()) {
            return;
        }
        gameServer.getConnection().onPingResponse();
        long l = System.currentTimeMillis() - this.pd;
        if (Math.abs(l) > 999L) {
            _log.warn("Gameserver " + gameServer.getId() + " [" + gameServer.getName() + "] : time offset " + l + " ms.");
        }
    }
}

