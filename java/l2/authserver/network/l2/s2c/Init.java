/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.L2LoginClient;
import l2.authserver.network.l2.s2c.L2LoginServerPacket;

public final class Init
extends L2LoginServerPacket {
    private int pp;
    private byte[] pz;
    private byte[] pl;

    public Init(L2LoginClient l2LoginClient) {
        this(l2LoginClient.getScrambledModulus(), l2LoginClient.getBlowfishKey(), l2LoginClient.getSessionId());
    }

    public Init(byte[] byArray, byte[] byArray2, int n) {
        this.pp = n;
        this.pz = byArray;
        this.pl = byArray2;
    }

    @Override
    protected void writeImpl() {
        this.writeC(0);
        this.writeD(this.pp);
        this.writeD(50721);
        this.writeB(this.pz);
        this.writeD(702387534);
        this.writeD(2009308412);
        this.writeD(-1750223328);
        this.writeD(129884407);
        this.writeB(this.pl);
        this.writeC(0);
    }
}

