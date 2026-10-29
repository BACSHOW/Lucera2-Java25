/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.s2c;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import l2.authserver.GameServerManager;
import l2.authserver.accounts.Account;
import l2.authserver.network.gamecomm.GameServer;
import l2.authserver.network.gamecomm.ProxyServer;
import l2.authserver.network.l2.s2c.L2LoginServerPacket;
import l2.commons.net.utils.NetUtils;

public final class ServerList
extends L2LoginServerPacket {
    private static final Comparator<ServerData> pH = new Comparator<ServerData>(){

        @Override
        public int compare(ServerData serverData, ServerData serverData2) {
            return serverData.serverId - serverData2.serverId;
        }
    };
    private final List<ServerData> pI = new ArrayList<ServerData>();
    private final int pJ;

    public ServerList(Account account) {
        this.pJ = account.getLastServer();
        String lastIp = account.getLastIP();
        for (GameServer gameServer : GameServerManager.getInstance().getGameServers()) {
            InetAddress inetAddress;
            try {
                // Dhousefe Loopback Rule: se o cliente conectou via loopback local (127.0.0.1 ou IPv6 ::1),
                // devolve 127.0.0.1 diretamente, permitindo conexão local imediata sem depender de IP externo ou DNS
                if ("127.0.0.1".equals(lastIp) || "0:0:0:0:0:0:0:1".equals(lastIp) || "::1".equals(lastIp)) {
                    inetAddress = InetAddress.getByName("127.0.0.1");
                } else {
                    inetAddress = NetUtils.isInternalIP(lastIp) ? gameServer.getInternalHost() : gameServer.getExternalHost();
                }
            }
            catch (UnknownHostException unknownHostException) {
                continue;
            }
            int n = gameServer.getPort();
            if (n <= 0 && gameServer.getHaProxyPort() > 0) {
                n = gameServer.getHaProxyPort();
            }
            ServerData serverData = new ServerData(gameServer.getId(), inetAddress, n, gameServer.isPvp(), gameServer.isShowingBrackets(), gameServer.getServerType(), gameServer.getOnline(), gameServer.getMaxPlayers(), gameServer.isOnline(), gameServer.getAgeLimit());
            List<ProxyServer> list = GameServerManager.getInstance().getProxyServersList(gameServer.getId());
            if (!list.isEmpty()) {
                LinkedList<ServerData> linkedList = new LinkedList<ServerData>();
                boolean bl = false;
                for (ProxyServer proxyServer : list) {
                    if (proxyServer.isHideMain()) {
                        bl = true;
                    }
                    if (proxyServer.getMinAccessLevel() > account.getAccessLevel()) continue;
                    linkedList.add(new ServerData(proxyServer.getProxyServerId(), proxyServer.getProxyAddr(), proxyServer.getProxyPort(), gameServer.isPvp(), gameServer.isShowingBrackets(), gameServer.getServerType(), gameServer.getOnline(), gameServer.getMaxPlayers(), gameServer.isOnline(), gameServer.getAgeLimit()));
                }
                if (!bl) {
                    this.pI.add(serverData);
                }
                this.pI.addAll(linkedList);
                continue;
            }
            this.pI.add(serverData);
        }
        Collections.sort(this.pI, pH);
    }

    @Override
    protected void writeImpl() {
        this.writeC(4);
        this.writeC(this.pI.size());
        this.writeC(this.pJ);
        for (ServerData serverData : this.pI) {
            this.writeC(serverData.serverId);
            InetAddress inetAddress = serverData.ip;
            byte[] byArray = inetAddress.getAddress();
            this.writeC(byArray[0] & 0xFF);
            this.writeC(byArray[1] & 0xFF);
            this.writeC(byArray[2] & 0xFF);
            this.writeC(byArray[3] & 0xFF);
            this.writeD(serverData.port);
            this.writeC(serverData.ageLimit);
            this.writeC(serverData.pvp ? 1 : 0);
            this.writeH(serverData.online);
            this.writeH(serverData.maxPlayers);
            this.writeC(serverData.status ? 1 : 0);
            this.writeD(serverData.type);
            this.writeC(serverData.brackets ? 1 : 0);
        }
    }

    private static class ServerData {
        int serverId;
        InetAddress ip;
        int port;
        int online;
        int maxPlayers;
        boolean status;
        boolean pvp;
        boolean brackets;
        int type;
        int ageLimit;

        ServerData(int n, InetAddress inetAddress, int n2, boolean bl, boolean bl2, int n3, int n4, int n5, boolean bl3, int n6) {
            this.serverId = n;
            this.ip = inetAddress;
            this.port = n2;
            this.pvp = bl;
            this.brackets = bl2;
            this.type = n3;
            this.online = n4;
            this.maxPlayers = n5;
            this.status = bl3;
            this.ageLimit = n6;
        }
    }
}

