/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;
import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.crypt.LoginCrypt;
import l2.authserver.crypt.ScrambledKeyPair;
import l2.authserver.network.l2.SessionKey;
import l2.authserver.network.l2.s2c.AccountKicked;
import l2.authserver.network.l2.s2c.L2LoginServerPacket;
import l2.authserver.network.l2.s2c.LoginFail;
import l2.commons.net.nio.impl.MMOClient;
import l2.commons.net.nio.impl.MMOConnection;
import l2.commons.net.nio.impl.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class L2LoginClient
extends MMOClient<MMOConnection<L2LoginClient>> {
    private static final Logger _log = LoggerFactory.getLogger(L2LoginClient.class);
    private LoginClientState pi = LoginClientState.CONNECTED;
    private LoginCrypt pj;
    private ScrambledKeyPair pk = Config.getScrambledRSAKeyPair();
    private byte[] pl = Config.getBlowfishKey();
    private String pm;
    private SessionKey pn;
    private Account po;
    private int pp;

    public L2LoginClient(MMOConnection<L2LoginClient> mMOConnection) {
        super(mMOConnection);
        this.pj = new LoginCrypt();
        this.pj.setKey(this.pl);
        this.pp = mMOConnection.hashCode();
    }

    @Override
    public boolean decrypt(ByteBuffer byteBuffer, int n) {
        boolean bl;
        try {
            bl = this.pj.decrypt(byteBuffer.array(), byteBuffer.position(), n);
        }
        catch (IOException iOException) {
            _log.error("", iOException);
            this.closeNow(true);
            return false;
        }
        if (!bl) {
            this.closeNow(true);
        }
        return bl;
    }

    @Override
    public boolean encrypt(ByteBuffer byteBuffer, int n) {
        int n2 = byteBuffer.position();
        try {
            n = this.pj.encrypt(byteBuffer.array(), n2, n);
        }
        catch (IOException iOException) {
            _log.error("", iOException);
            return false;
        }
        ((Buffer)byteBuffer).position(n2 + n);
        return true;
    }

    public LoginClientState getState() {
        return this.pi;
    }

    public void setState(LoginClientState loginClientState) {
        this.pi = loginClientState;
    }

    public byte[] getBlowfishKey() {
        return this.pl;
    }

    public byte[] getScrambledModulus() {
        return this.pk.getScrambledModulus();
    }

    public RSAPrivateKey getRSAPrivateKey() {
        return (RSAPrivateKey)this.pk.getKeyPair().getPrivate();
    }

    public String getLogin() {
        return this.pm;
    }

    public void setLogin(String string) {
        this.pm = string;
    }

    public Account getAccount() {
        return this.po;
    }

    public void setAccount(Account account) {
        this.po = account;
    }

    public SessionKey getSessionKey() {
        return this.pn;
    }

    public void setSessionKey(SessionKey sessionKey) {
        this.pn = sessionKey;
    }

    public void setSessionId(int n) {
        this.pp = n;
    }

    public int getSessionId() {
        return this.pp;
    }

    public void sendPacket(L2LoginServerPacket l2LoginServerPacket) {
        if (this.isConnected()) {
            ((MMOConnection)((Object)this.getConnection())).sendPacket((SendablePacket<L2LoginClient>)l2LoginServerPacket);
        }
    }

    public void close(LoginFail.LoginFailReason loginFailReason) {
        if (this.isConnected()) {
            ((MMOConnection)((Object)this.getConnection())).close(new LoginFail(loginFailReason));
        }
    }

    public void close(AccountKicked.AccountKickedReason accountKickedReason) {
        if (this.isConnected()) {
            ((MMOConnection)((Object)this.getConnection())).close(new AccountKicked(accountKickedReason));
        }
    }

    public void close(L2LoginServerPacket l2LoginServerPacket) {
        if (this.isConnected()) {
            ((MMOConnection)((Object)this.getConnection())).close(l2LoginServerPacket);
        }
    }

    @Override
    public void onDisconnection() {
        this.pi = LoginClientState.DISCONNECTED;
        this.pn = null;
        this.pj = null;
        this.pk = null;
        this.pl = null;
    }

    public String toString() {
        switch (this.pi) {
            case AUTHED: {
                return "[ Account : " + this.getLogin() + " IP: " + this.getIpAddr() + "]";
            }
        }
        return "[ State : " + (Object)((Object)this.getState()) + " IP: " + this.getIpAddr() + "]";
    }

    @Override
    protected void onForcedDisconnection() {
    }

    public static enum LoginClientState {
        CONNECTED,
        AUTHED_GG,
        AUTHED,
        DISCONNECTED;

    }
}
