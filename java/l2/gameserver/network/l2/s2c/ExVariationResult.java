/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExVariationResult
extends L2GameServerPacket {
    public static final ExVariationResult FAIL_PACKET = new ExVariationResult(0, 0, 0);
    private int aiv;
    private int aiw;
    private int oJ;

    public ExVariationResult(int n, int n2, int n3) {
        this.aiv = n;
        this.aiw = n2;
        this.oJ = n3;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(85);
        this.writeD(this.aiv);
        this.writeD(this.aiw);
        this.writeD(this.oJ);
    }
}

