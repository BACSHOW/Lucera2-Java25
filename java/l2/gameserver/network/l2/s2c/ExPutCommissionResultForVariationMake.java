/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPutCommissionResultForVariationMake
extends L2GameServerPacket {
    public static final ExPutCommissionResultForVariationMake FAIL_PACKET = new ExPutCommissionResultForVariationMake();
    private int ahu;
    private int pv;
    private int oJ;
    private long abE;
    private long ahv;

    private ExPutCommissionResultForVariationMake() {
        this.ahu = 0;
        this.pv = 1;
        this.abE = 0L;
        this.ahv = 0L;
        this.oJ = 0;
    }

    public ExPutCommissionResultForVariationMake(int n, long l) {
        this.ahu = n;
        this.pv = 1;
        this.abE = l;
        this.ahv = l;
        this.oJ = 1;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(84);
        this.writeD(this.ahu);
        this.writeD(this.pv);
        this.writeD((int)this.abE);
        this.writeD((int)this.ahv);
        this.writeD(this.oJ);
    }
}

