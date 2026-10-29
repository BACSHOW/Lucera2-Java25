/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.as2gs;

import l2.authserver.network.gamecomm.SendablePacket;

public class PingRequest
extends SendablePacket {
    @Override
    protected void writeImpl() {
        this.writeC(255);
    }
}

