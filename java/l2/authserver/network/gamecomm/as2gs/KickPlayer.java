/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.gamecomm.as2gs;

import l2.authserver.network.gamecomm.SendablePacket;

public class KickPlayer
extends SendablePacket {
    private String account;

    public KickPlayer(String string) {
        this.account = string;
    }

    @Override
    protected void writeImpl() {
        this.writeC(3);
        this.writeS(this.account);
    }
}

