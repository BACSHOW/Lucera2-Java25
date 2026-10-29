/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.c2s;

import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.SessionKey;
import l2.authserver.network.l2.c2s.L2LoginClientPacket;
import l2.authserver.network.l2.s2c.LoginFail;
import l2.authserver.network.l2.s2c.ServerList;

public class RequestServerList
extends L2LoginClientPacket {
    private int pt;
    private int pu;

    @Override
    protected void readImpl() {
        this.pt = this.readD();
        this.pu = this.readD();
        if (this.getAvaliableBytes() >= 1) {
            this.readC();
        }
    }

    @Override
    protected void runImpl() {
        L2LoginClient l2LoginClient = (L2LoginClient)this.getClient();
        SessionKey sessionKey = l2LoginClient.getSessionKey();
        if (sessionKey == null || !sessionKey.checkLoginPair(this.pt, this.pu)) {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }
        l2LoginClient.sendPacket(new ServerList(l2LoginClient.getAccount()));
    }
}

