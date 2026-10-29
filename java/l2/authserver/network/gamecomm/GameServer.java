/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import l2.authserver.Config;
import l2.authserver.network.gamecomm.GameServerConnection;
import l2.authserver.network.gamecomm.SendablePacket;

public class GameServer {
    private int _id;
    private String of;
    private String og;
    private InetAddress oh;
    private InetAddress oi;
    private volatile int[] oj = new int[]{7777};
    private volatile int ok;
    private int ol;
    private int om;
    private int on;
    private boolean oo;
    private boolean op;
    private boolean oq;
    private boolean or;
    private int os;
    private GameServerConnection ot;
    private boolean ou;
    private AtomicInteger ov = new AtomicInteger(0);
    private volatile int ow;

    public GameServer(GameServerConnection gameServerConnection) {
        this.ot = gameServerConnection;
    }

    public GameServer(int n) {
        this._id = n;
    }

    public void setId(int n) {
        this._id = n;
    }

    public int getId() {
        return this._id;
    }

    public void setAuthed(boolean bl) {
        this.ou = bl;
    }

    public boolean isAuthed() {
        return this.ou;
    }

    public void setConnection(GameServerConnection gameServerConnection) {
        this.ot = gameServerConnection;
    }

    public GameServerConnection getConnection() {
        return this.ot;
    }

    public InetAddress getInternalHost() throws UnknownHostException {
        if (this.oh != null) {
            return this.oh;
        }
        this.oh = InetAddress.getByName(this.of);
        return this.oh;
    }

    public void setInternalHost(String string) {
        if (string.equals("*")) {
            string = this.getConnection().getIpAddress();
        }
        this.of = string;
        this.oh = null;
    }

    public void setExternalHost(String string) {
        if (string.equals("*")) {
            string = this.getConnection().getIpAddress();
        }
        this.og = string;
        this.oi = null;
    }

    public InetAddress getExternalHost() throws UnknownHostException {
        if (this.oi != null) {
            return this.oi;
        }
        this.oi = InetAddress.getByName(this.og);
        return this.oi;
    }

    public int getPort() {
        int[] nArray = this.oj;
        if (nArray.length == 0) {
            return -1;
        }
        return nArray[(this.ov.incrementAndGet() & Integer.MAX_VALUE) % nArray.length];
    }

    public void setPorts(int[] nArray) {
        this.oj = nArray;
    }

    public int getHaProxyPort() {
        return this.ok;
    }

    public void setHaProxyPort(int n) {
        this.ok = n;
    }

    public void setMaxPlayers(int n) {
        this.os = n;
    }

    public int getMaxPlayers() {
        return this.os;
    }

    public int getOnline() {
        return this.ow;
    }

    public void addAccount(String string) {
        ++this.ow;
    }

    public void removeAccount(String string) {
        --this.ow;
    }

    public void setDown() {
        this.setAuthed(false);
        this.setConnection(null);
        this.setOnline(false);
    }

    public String getName() {
        return Config.SERVER_NAMES.get(this.getId());
    }

    public void sendPacket(SendablePacket sendablePacket) {
        GameServerConnection gameServerConnection = this.getConnection();
        if (gameServerConnection != null) {
            gameServerConnection.sendPacket(sendablePacket);
        }
    }

    public int getServerType() {
        return this.ol;
    }

    public boolean isOnline() {
        return this.oo;
    }

    public void setOnline(boolean bl) {
        this.oo = bl;
    }

    public void setServerType(int n) {
        this.ol = n;
    }

    public boolean isPvp() {
        return this.op;
    }

    public void setPvp(boolean bl) {
        this.op = bl;
    }

    public boolean isShowingBrackets() {
        return this.oq;
    }

    public void setShowingBrackets(boolean bl) {
        this.oq = bl;
    }

    public boolean isGmOnly() {
        return this.or;
    }

    public void setGmOnly(boolean bl) {
        this.or = bl;
    }

    public int getAgeLimit() {
        return this.om;
    }

    public void setAgeLimit(int n) {
        this.om = n;
    }

    public void setProtocol(int n) {
        this.on = n;
    }
}

