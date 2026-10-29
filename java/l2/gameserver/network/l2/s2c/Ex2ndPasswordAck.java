/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class Ex2ndPasswordAck
extends L2GameServerPacket {
    private final Ex2ndPasswordAckResult afR;
    private final int afS;

    public Ex2ndPasswordAck(Ex2ndPasswordAckResult ex2ndPasswordAckResult) {
        this.afR = ex2ndPasswordAckResult;
        this.afS = 0;
    }

    public Ex2ndPasswordAck(Ex2ndPasswordAckResult ex2ndPasswordAckResult, int n) {
        this.afR = ex2ndPasswordAckResult;
        this.afS = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(231);
        this.writeC(this.afR.getArg0());
        this.writeD(this.afR.getArg1());
        this.writeD(this.afS);
    }

    public static enum Ex2ndPasswordAckResult {
        SUCCESS_CREATE(0, 0),
        SUCCESS_VERIFY(2, 0),
        FAIL_CREATE(0, 1),
        FAIL_VERIFY(2, 1),
        BLOCK_HOMEPAGE(0, 2),
        ERROR(3, 0);

        private int afT;
        private int afU;

        private Ex2ndPasswordAckResult(int n2, int n3) {
            this.afT = n2;
            this.afU = n3;
        }

        public int getArg0() {
            return this.afT;
        }

        public int getArg1() {
            return this.afU;
        }
    }
}

