/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.s2c.L2LoginServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GGAuth
extends L2LoginServerPacket {
    static Logger _log = LoggerFactory.getLogger(GGAuth.class);
    public static int SKIP_GG_AUTH_REQUEST = 11;
    private int py;

    public GGAuth(int n) {
        this.py = n;
    }

    @Override
    protected void writeImpl() {
        this.writeC(11);
        this.writeD(this.py);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
    }
}

