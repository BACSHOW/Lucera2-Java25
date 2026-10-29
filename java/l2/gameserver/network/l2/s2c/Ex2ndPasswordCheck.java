/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class Ex2ndPasswordCheck
extends L2GameServerPacket {
    private final int afT;
    private final int afU;

    public Ex2ndPasswordCheck(Ex2ndPasswordCheckResult ex2ndPasswordCheckResult) {
        this.afT = ex2ndPasswordCheckResult.getArg0();
        this.afU = ex2ndPasswordCheckResult.getArg1();
    }

    public Ex2ndPasswordCheck(Ex2ndPasswordCheckResult ex2ndPasswordCheckResult, int n) {
        this.afT = ex2ndPasswordCheckResult.getArg0();
        this.afU = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(229);
        this.writeD(this.afT);
        this.writeD(this.afU);
    }

    public static enum Ex2ndPasswordCheckResult {
        CREATE(0, 0),
        CHECK(1, 0),
        BLOCK_TIME(1),
        SUCCESS(2, 0),
        ERROR(3, 0);

        private final int afT;
        private final int afU;

        private Ex2ndPasswordCheckResult(int n2, int n3) {
            this.afT = n2;
            this.afU = n3;
        }

        private Ex2ndPasswordCheckResult(int n2) {
            this.afT = n2;
            this.afU = -1;
        }

        public int getArg0() {
            return this.afT;
        }

        public int getArg1() {
            return this.afU;
        }
    }
}

