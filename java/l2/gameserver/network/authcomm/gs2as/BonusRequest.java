/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import l2.gameserver.network.authcomm.SendablePacket;

public class BonusRequest
extends SendablePacket {
    private String account;
    private double oL;
    private int oM;

    public BonusRequest(String string, double d, int n) {
        this.account = string;
        this.oL = d;
        this.oM = n;
    }

    @Override
    protected void writeImpl() {
        this.writeC(16);
        this.writeS(this.account);
        this.writeF(this.oL);
        this.writeD(this.oM);
    }
}

