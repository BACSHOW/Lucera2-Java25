/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ReceivablePacket;

public class PlayerLogout
extends ReceivablePacket {
    private String account;

    @Override
    protected void readImpl() {
        this.account = this.readS();
    }

    @Override
    protected void runImpl() {
        GameServer gameServer = this.getGameServer();
        if (gameServer.isAuthed()) {
            gameServer.removeAccount(this.account);
        }
    }
}

