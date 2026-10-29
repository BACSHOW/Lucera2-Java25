/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.gs2as;

import java.util.ArrayList;
import java.util.List;
import l2.authserver.accounts.Account;
import l2.authserver.network.gamecomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeAccessLevelMulti
extends ReceivablePacket {
    public static final Logger _log = LoggerFactory.getLogger(ChangeAccessLevelMulti.class);
    private List<String> oX;
    private int level;
    private int nw;

    @Override
    protected void readImpl() {
        int n = Math.min(this.readD(), 127);
        this.oX = new ArrayList<String>(n);
        for (int i = 0; i < n; ++i) {
            this.oX.add(this.readS());
        }
        this.level = this.readD();
        this.nw = this.readD();
    }

    @Override
    protected void runImpl() {
        if (this.oX == null || this.oX.isEmpty()) {
            return;
        }
        for (String string : this.oX) {
            Account account = new Account(string);
            account.restore();
            account.setAccessLevel(this.level);
            account.setBanExpire(this.nw);
            account.update();
        }
    }
}

