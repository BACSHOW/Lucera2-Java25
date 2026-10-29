/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class Ex2ndPasswordVerify
extends L2GameServerPacket {
    private final Ex2ndPasswordVerifyResult afX;
    private final int afY;

    public Ex2ndPasswordVerify(Ex2ndPasswordVerifyResult ex2ndPasswordVerifyResult) {
        this.afX = ex2ndPasswordVerifyResult;
        this.afY = 0;
    }

    public Ex2ndPasswordVerify(Ex2ndPasswordVerifyResult ex2ndPasswordVerifyResult, int n) {
        this.afX = ex2ndPasswordVerifyResult;
        this.afY = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(230);
        this.writeD(this.afX.getVal());
        this.writeD(this.afY);
    }

    public static enum Ex2ndPasswordVerifyResult {
        SUCCESS(0),
        FAILED(1),
        BLOCK_HOMEPAGE(2),
        ERROR(3);

        private int afZ;

        private Ex2ndPasswordVerifyResult(int n2) {
            this.afZ = n2;
        }

        public int getVal() {
            return this.afZ;
        }
    }
}

