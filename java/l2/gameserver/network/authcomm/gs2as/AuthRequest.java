/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.Config;
import l2.gameserver.network.authcomm.SendablePacket;

public class AuthRequest
extends SendablePacket {
    @Override
    protected void writeImpl() {
        this.writeC(0);
        this.writeD(2);
        this.writeC(Config.REQUEST_ID);
        this.writeC(Config.ACCEPT_ALTERNATE_ID ? 1 : 0);
        this.writeD(Config.AUTH_SERVER_SERVER_TYPE);
        this.writeD(Config.AUTH_SERVER_AGE_LIMIT);
        this.writeC(Config.AUTH_SERVER_GM_ONLY ? 1 : 0);
        this.writeC(Config.AUTH_SERVER_BRACKETS ? 1 : 0);
        this.writeC(Config.AUTH_SERVER_IS_PVP ? 1 : 0);
        this.writeS(Config.EXTERNAL_HOSTNAME);
        this.writeS(Config.INTERNAL_HOSTNAME);
        this.writeH(Config.PORTS_GAME.length);
        for (int n : Config.PORTS_GAME) {
            this.writeH(n);
        }
        this.writeD(Config.MAXIMUM_ONLINE_USERS);
        this.writeH(Config.HAPROXY_PORT_GAME);
    }
}

