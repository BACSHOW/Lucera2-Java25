/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class OnlineStatus
extends SendablePacket {
    private boolean pb;

    public OnlineStatus(boolean bl) {
        this.pb = bl;
    }

    @Override
    protected void writeImpl() {
        this.writeC(1);
        this.writeC(this.pb ? 1 : 0);
    }
}

