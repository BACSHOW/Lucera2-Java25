/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.accounts.SessionManager;
import l2.authserver.network.gamecomm.ReceivablePacket;
import l2.authserver.network.gamecomm.as2gs.PlayerAuthResponse;
import l2.authserver.network.l2.SessionKey;

public class PlayerAuthRequest
extends ReceivablePacket {
    private String account;
    private int pe;
    private int pf;
    private int pg;
    private int ph;

    @Override
    protected void readImpl() {
        this.account = this.readS();
        this.pe = this.readD();
        this.pf = this.readD();
        this.pg = this.readD();
        this.ph = this.readD();
    }

    @Override
    protected void runImpl() {
        SessionKey sessionKey = new SessionKey(this.pg, this.ph, this.pe, this.pf);
        SessionManager.Session session = SessionManager.getInstance().closeSession(sessionKey);
        if (session == null || !session.getAccount().getLogin().equals(this.account)) {
            this.sendPacket(new PlayerAuthResponse(this.account));
            return;
        }
        this.sendPacket(new PlayerAuthResponse(session, session.getSessionKey().equals(sessionKey), session.getAccount().getLastServer()));
    }
}

