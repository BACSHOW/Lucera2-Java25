/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.SessionKey;
import l2.authserver.network.l2.s2c.L2LoginServerPacket;

public final class PlayOk
extends L2LoginServerPacket {
    private int pF;
    private int pG;

    public PlayOk(SessionKey sessionKey) {
        this.pF = sessionKey.playOkID1;
        this.pG = sessionKey.playOkID2;
    }

    @Override
    protected void writeImpl() {
        this.writeC(7);
        this.writeD(this.pF);
        this.writeD(this.pG);
    }
}

