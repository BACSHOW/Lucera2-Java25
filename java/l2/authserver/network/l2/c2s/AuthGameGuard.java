/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.c2s;

import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.c2s.L2LoginClientPacket;
import l2.authserver.network.l2.s2c.GGAuth;
import l2.authserver.network.l2.s2c.LoginFail;

public class AuthGameGuard
extends L2LoginClientPacket {
    private int pp;

    @Override
    protected void readImpl() {
        this.pp = this.readD();
    }

    @Override
    protected void runImpl() {
        L2LoginClient l2LoginClient = (L2LoginClient)this.getClient();
        if (this.pp == 0 || this.pp == l2LoginClient.getSessionId()) {
            l2LoginClient.setState(L2LoginClient.LoginClientState.AUTHED_GG);
            l2LoginClient.sendPacket(new GGAuth(l2LoginClient.getSessionId()));
        } else {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
        }
    }
}

