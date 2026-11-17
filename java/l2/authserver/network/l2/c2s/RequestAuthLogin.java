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
        String ip = l2LoginClient.getIpAddr();
        // IP bloqueado: so mostra a mensagem (nem decifra a senha, nem conta tentativa)
        int banLevel = l2.authserver.LoginGuard.getInstance().getActiveBanLevel(ip);
        if (banLevel >= 0) {
            l2.authserver.LoginMetrics.LOGINS_FAILED.increment();
            RequestAuthLogin.fail(l2LoginClient, RequestAuthLogin.ipBannedCode(banLevel));
            return;
        }
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
        // Conta de teste das mensagens: senha = numero do codigo (1 a 63), nada e contado
        if (!Config.MSG_TEST_ACCOUNT.isEmpty() && Config.MSG_TEST_ACCOUNT.equals(object)) {
            int code;
            try {
                code = Integer.parseInt(string.trim());
            }
            catch (NumberFormatException e) {
                code = 0;
            }
            if (code >= 1 && code <= 63) {
                org.slf4j.LoggerFactory.getLogger(RequestAuthLogin.class).info("Teste de mensagem do login: codigo " + code + " enviado para " + ip + ".");
                RequestAuthLogin.fail(l2LoginClient, code);
                return;
            }
        }
        Account account = new Account((String)object);
        account.restore();
        String string2 = Config.DEFAULT_CRYPT.encrypt(string);
        if (account.getPasswordHash() == null) {
            if (Config.AUTO_CREATE_ACCOUNTS && ((String)object).matches(Config.ANAME_TEMPLATE) && string.matches(Config.APASSWD_TEMPLATE)) {
                account.setPasswordHash(string2);
                account.save();
                l2.authserver.LoginMetrics.ACCOUNTS_CREATED.increment();
            } else {
                // Conta inexistente tambem conta como tentativa errada: sem isso, testar nomes
                // aleatorios nunca levava a ban.
                l2.authserver.LoginMetrics.LOGINS_FAILED.increment();
                if (!IpBanManager.getInstance().tryLogin(ip, false)) {
                    RequestAuthLogin.failBanned(l2LoginClient, ip);
                    return;
                }
                if (Config.AUTO_CREATE_ACCOUNTS && !((String)object).matches(Config.ANAME_TEMPLATE)) {
                    RequestAuthLogin.fail(l2LoginClient, Config.MSG_INVALID_ACCOUNT_NAME);
                } else if (Config.AUTO_CREATE_ACCOUNTS) {
                    RequestAuthLogin.fail(l2LoginClient, Config.MSG_INVALID_PASSWORD);
                } else {
                    RequestAuthLogin.failWrongPassword(l2LoginClient, ip);
                }
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
        if (!IpBanManager.getInstance().tryLogin(ip, bl)) {
            l2.authserver.LoginMetrics.LOGINS_FAILED.increment();
            RequestAuthLogin.failBanned(l2LoginClient, ip);
            return;
        }
        if (!bl) {
            l2.authserver.LoginMetrics.LOGINS_FAILED.increment();
            RequestAuthLogin.failWrongPassword(l2LoginClient, ip);
            return;
        }
        if (account.getAccessLevel() < 0) {
            RequestAuthLogin.fail(l2LoginClient, Config.MSG_ACCOUNT_BANNED);
            return;
        }
        if (account.getBanExpire() > n2) {
            RequestAuthLogin.fail(l2LoginClient, Config.MSG_ACCOUNT_BANNED);
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
        l2.authserver.LoginMetrics.LOGINS_OK.increment();
        l2LoginClient.sendPacket(new LoginOk(l2LoginClient.getSessionKey()));
    }

    private static void fail(L2LoginClient client, int code) {
        client.close(new LoginFail(code));
    }

    /** Senha errada; se a proxima errada causar o ban, avisa que e a ultima tentativa. */
    private static void failWrongPassword(L2LoginClient client, String ip) {
        int left = IpBanManager.getInstance().remainingTries(ip);
        RequestAuthLogin.fail(client, left == 1 ? Config.MSG_LAST_ATTEMPT : LoginFail.LoginFailReason.REASON_USER_OR_PASS_WRONG.getCode());
    }

    /** O IP acabou de ser banido por esta tentativa. */
    private static void failBanned(L2LoginClient client, String ip) {
        int level = l2.authserver.LoginGuard.getInstance().getActiveBanLevel(ip);
        RequestAuthLogin.fail(client, RequestAuthLogin.ipBannedCode(Math.max(0, level)));
    }

    /** Codigo da mensagem para o nivel do ban (LoginMsgIpBanned: um por nivel, o ultimo vale para os seguintes). */
    private static int ipBannedCode(int level) {
        int[] codes = Config.MSG_IP_BANNED;
        return codes[Math.min(level, codes.length - 1)];
    }
}
