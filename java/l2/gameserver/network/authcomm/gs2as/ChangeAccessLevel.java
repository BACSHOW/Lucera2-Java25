/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class ChangeAccessLevel
extends SendablePacket {
    private String account;
    private int level;
    private int nw;

    public ChangeAccessLevel(String string, int n, int n2) {
        this.account = string;
        this.level = n;
        this.nw = n2;
    }

    @Override
    protected void writeImpl() {
        this.writeC(17);
        this.writeS(this.account);
        this.writeD(this.level);
        this.writeD(this.nw);
    }
}

