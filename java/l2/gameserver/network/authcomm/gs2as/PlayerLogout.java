/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class PlayerLogout
extends SendablePacket {
    private String account;

    public PlayerLogout(String string) {
        this.account = string;
    }

    @Override
    protected void writeImpl() {
        this.writeC(4);
        this.writeS(this.account);
    }
}

