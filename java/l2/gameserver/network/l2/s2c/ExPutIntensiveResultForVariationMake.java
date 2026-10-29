/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPutIntensiveResultForVariationMake
extends L2GameServerPacket {
    public static final ExPutIntensiveResultForVariationMake FAIL_PACKET = new ExPutIntensiveResultForVariationMake(0, 0, 0, 0L, false);
    private int abC;
    private int ahw;
    private int ahx;
    private int oJ;
    private long abE;

    public ExPutIntensiveResultForVariationMake(int n, int n2, int n3, long l, boolean bl) {
        this.abC = n;
        this.ahw = n2;
        this.ahx = n3;
        this.abE = l;
        this.oJ = bl ? 1 : 0;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(83);
        this.writeD(this.abC);
        this.writeD(this.ahw);
        this.writeD(this.ahx);
        this.writeD((int)this.abE);
        this.writeD(this.oJ);
    }
}

