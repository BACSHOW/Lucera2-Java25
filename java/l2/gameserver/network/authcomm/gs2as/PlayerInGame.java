/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class PlayerInGame
extends SendablePacket {
    private String account;

    public PlayerInGame(String string) {
        this.account = string;
    }

    @Override
    protected void writeImpl() {
        this.writeC(3);
        this.writeS(this.account);
    }
}

