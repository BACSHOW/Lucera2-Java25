/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.c2s;

import javax.crypto.Cipher;
import l2.authserver.Config;
import l2.authserver.IpBanManager;
import l2.authserver.accounts.Account;
import l2.authserver.accounts.SessionManager;
import l2.authserver.crypt.PasswordHash;
import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.c2s.L2LoginClientPacket;
import l2.authserver.network.l2.s2c.LoginFail;
import l2.authserver.network.l2.s2c.LoginOk;
import l2.authserver.utils.Log;

public class RequestAuthLogin
extends L2LoginClientPacket {
    private byte[] ps = new byte[128];

    @Override
    protected void readImpl() {
        this.readB(this.ps);
        if (this.getAvaliableBytes() >= 4) this.readD();
        if (this.getAvaliableBytes() >= 4) this.readD();
        if (this.getAvaliableBytes() >= 4) this.readD();
        if (this.getAvaliableBytes() >= 4) this.readD();
        if (this.getAvaliableBytes() >= 4) this.readD();
        if (this.getAvaliableBytes() >= 4) this.readD();
        if (this.getAvaliableBytes() >= 2) this.readH();
        if (this.getAvaliableBytes() >= 1) this.readC();
    }

    @Override
    protected void runImpl() throws Exception {
        boolean bl;
        byte[] byArray;
        Object object;
        L2LoginClient l2LoginClient = (L2LoginClient)this.getClient();
        try {
            object = Cipher.getInstance("RSA/ECB/nopadding");
            ((Cipher)object).init(2, l2LoginClient.getRSAPrivateKey());
            byArray = ((Cipher)object).doFinal(this.ps, 0, 128);
        }
        catch (Exception exception) {
            l2LoginClient.closeNow(true);
            return;
        }
        object = new String(byArray, 94, 14).trim();
        object = ((String)object).toLowerCase();
        String string = new String(byArray, 108, 16).trim();
        int n = byArray[124] & 0xFF;
        n |= (byArray[125] & 0xFF) << 8;
        n |= (byArray[126] & 0xFF) << 16;
        n |= (byArray[127] & 0xFF) << 24;
        int n2 = (int)(System.currentTimeMillis() / 1000L);
        Account account = new Account((String)object);
        account.restore();
        String string2 = Config.DEFAULT_CRYPT.encrypt(string);
        if (account.getPasswordHash() == null) {
            if (Config.AUTO_CREATE_ACCOUNTS && ((String)object).matches(Config.ANAME_TEMPLATE) && string.matches(Config.APASSWD_TEMPLATE)) {
                account.setPasswordHash(string2);
                account.save();
            } else {
                l2LoginClient.close(LoginFail.LoginFailReason.REASON_USER_OR_PASS_WRONG);
                return;
            }
        }
        if (!(bl = account.getPasswordHash().equalsIgnoreCase(string2))) {
            for (PasswordHash passwordHash : Config.LEGACY_CRYPT) {
                if (!passwordHash.compare(string, account.getPasswordHash())) continue;
                bl = true;
                account.setPasswordHash(string2);
                break;
            }
        }
        if (!IpBanManager.getInstance().tryLogin(l2LoginClient.getIpAddr(), bl)) {
            l2LoginClient.closeNow(false);
            return;
        }
        if (!bl) {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_USER_OR_PASS_WRONG);
            return;
        }
        if (account.getAccessLevel() < 0) {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }
        if (account.getBanExpire() > n2) {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }
        if (!account.isAllowedIP(l2LoginClient.getIpAddr())) {
            l2LoginClient.close(LoginFail.LoginFailReason.REASON_ATTEMPTED_RESTRICTED_IP);
            return;
        }
        account.setLastAccess(n2);
        account.setLastIP(l2LoginClient.getIpAddr());
        Log.LogAccount(account);
        SessionManager.Session session = SessionManager.getInstance().openSession(account);
        l2LoginClient.setAuthed(true);
        l2LoginClient.setLogin((String)object);
        l2LoginClient.setAccount(account);
        l2LoginClient.setSessionKey(session.getSessionKey());
        l2LoginClient.setState(L2LoginClient.LoginClientState.AUTHED);
        l2LoginClient.sendPacket(new LoginOk(l2LoginClient.getSessionKey()));
    }
}

