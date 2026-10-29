/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.as2gs;

import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.ReceivablePacket;
import l2.gameserver.network.authcomm.gs2as.OnlineStatus;
import l2.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthResponse
extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(AuthResponse.class);
    private int pv;
    private String ZA;

    @Override
    protected void readImpl() {
        this.pv = this.readC();
        this.ZA = this.readS();
    }

    @Override
    protected void runImpl() {
        String[] stringArray;
        _log.info("Registered on authserver as " + this.pv + " [" + this.ZA + "]");
        this.sendPacket(new OnlineStatus(true));
        for (String string : stringArray = AuthServerCommunication.getInstance().getAccounts()) {
            this.sendPacket(new PlayerInGame(string));
        }
    }
}

