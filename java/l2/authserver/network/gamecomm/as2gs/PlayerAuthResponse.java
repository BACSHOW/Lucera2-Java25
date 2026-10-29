/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.as2gs;

import l2.authserver.accounts.Account;
import l2.authserver.accounts.SessionManager;
import l2.authserver.network.gamecomm.SendablePacket;
import l2.authserver.network.l2.SessionKey;

public class PlayerAuthResponse
extends SendablePacket {
    private String ns;
    private boolean oK;
    private int playOkID1;
    private int playOkID2;
    private int loginOkID1;
    private int loginOkID2;
    private double oL;
    private int oM;
    private int oN;

    public PlayerAuthResponse(SessionManager.Session session, boolean bl, int n) {
        Account account = session.getAccount();
        this.ns = account.getLogin();
        this.oK = bl;
        if (bl) {
            SessionKey sessionKey = session.getSessionKey();
            this.playOkID1 = sessionKey.playOkID1;
            this.playOkID2 = sessionKey.playOkID2;
            this.loginOkID1 = sessionKey.loginOkID1;
            this.loginOkID2 = sessionKey.loginOkID2;
            this.oN = n;
        }
    }

    public PlayerAuthResponse(String string) {
        this.ns = string;
        this.oK = false;
    }

    @Override
    protected void writeImpl() {
        this.writeC(2);
        this.writeS(this.ns);
        this.writeC(this.oK ? 1 : 0);
        if (this.oK) {
            this.writeD(this.playOkID1);
            this.writeD(this.playOkID2);
            this.writeD(this.loginOkID1);
            this.writeD(this.loginOkID2);
            this.writeD(this.oN);
        }
    }
}

