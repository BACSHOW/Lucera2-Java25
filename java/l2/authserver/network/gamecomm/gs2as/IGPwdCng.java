/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.gs2as;

import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.crypt.PasswordHash;
import l2.authserver.network.gamecomm.ReceivablePacket;
import l2.authserver.network.gamecomm.as2gs.NotifyPwdCngResult;

public class IGPwdCng
extends ReceivablePacket {
    private int oI;
    private String oY;
    private String oZ;
    private String pa;

    @Override
    protected void readImpl() {
        this.oI = this.readD();
        this.oY = this.readS();
        this.oZ = this.readS();
        this.pa = this.readS();
    }

    @Override
    protected void runImpl() {
        try {
            Account account = new Account(this.oY);
            account.restore();
            if (account.getPasswordHash() == null) {
                this.sendPacket(new NotifyPwdCngResult(this.oI, 4));
                return;
            }
            if (!this.pa.matches(Config.APASSWD_TEMPLATE)) {
                this.sendPacket(new NotifyPwdCngResult(this.oI, 3));
                return;
            }
            boolean bl = Config.DEFAULT_CRYPT.compare(this.oZ, account.getPasswordHash());
            if (!bl) {
                for (PasswordHash passwordHash : Config.LEGACY_CRYPT) {
                    if (!passwordHash.compare(this.oZ, account.getPasswordHash())) continue;
                    bl = true;
                    break;
                }
            }
            if (!bl) {
                this.sendPacket(new NotifyPwdCngResult(this.oI, 2));
                return;
            }
            account.setPasswordHash(Config.DEFAULT_CRYPT.encrypt(this.pa));
            account.update();
            this.sendPacket(new NotifyPwdCngResult(this.oI, 1));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

