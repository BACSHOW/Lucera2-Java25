/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm;

import java.net.InetAddress;
import l2.authserver.GameServerManager;
import l2.authserver.network.gamecomm.GameServer;

public class ProxyServer {
    private final int mV;
    private final int mW;
    private InetAddress oF;
    private int mY;
    private boolean oG;
    private int mZ;

    public ProxyServer(int n, int n2) {
        this.mV = n;
        this.mW = n2;
    }

    public int getOrigServerId() {
        return this.mV;
    }

    public int getProxyServerId() {
        return this.mW;
    }

    public InetAddress getProxyAddr() {
        return this.oF;
    }

    public void setProxyAddr(InetAddress inetAddress) {
        this.oF = inetAddress;
    }

    public int getProxyPort() {
        return this.mY;
    }

    public void setProxyPort(int n) {
        this.mY = n;
    }

    public GameServer getGameServer() {
        return GameServerManager.getInstance().getGameServerById(this.getOrigServerId());
    }

    public boolean isHideMain() {
        return this.oG;
    }

    public ProxyServer setHideMain(boolean bl) {
        this.oG = bl;
        return this;
    }

    public int getMinAccessLevel() {
        return this.mZ;
    }

    public ProxyServer setMinAccessLevel(int n) {
        this.mZ = n;
        return this;
    }
}

