/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.as2gs;

import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServerFail
extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(LoginServerFail.class);
    private static final String[] ZB = new String[]{"none", "IP banned", "IP reserved", "wrong hexid", "ID reserved", "no free ID", "not authed", "already logged in"};
    private int pE;

    public String getReason() {
        return ZB[this.pE];
    }

    @Override
    protected void readImpl() {
        this.pE = this.readC();
    }

    @Override
    protected void runImpl() {
        _log.warn("Authserver registration failed! Reason: " + this.getReason());
        AuthServerCommunication.getInstance().restart();
    }
}

