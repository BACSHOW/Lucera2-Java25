/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class PingResponse
extends SendablePacket {
    @Override
    protected void writeImpl() {
        this.writeC(255);
        this.writeQ(System.currentTimeMillis());
    }
}

