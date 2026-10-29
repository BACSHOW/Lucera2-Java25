/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.s2c.L2LoginServerPacket;

public final class PlayFail
extends L2LoginServerPacket {
    public static final int REASON_SYSTEM_ERROR = 1;
    public static final int REASON_ACCESS_FAILED_1 = 2;
    public static final int REASON_ACCOUNT_INFO_INCORRECT = 3;
    public static final int REASON_PASSWORD_INCORRECT_1 = 4;
    public static final int REASON_PASSWORD_INCORRECT_2 = 5;
    public static final int REASON_NO_REASON = 6;
    public static final int REASON_SYS_ERROR = 7;
    public static final int REASON_ACCESS_FAILED_2 = 8;
    public static final int REASON_HIGH_SERVER_TRAFFIC = 9;
    public static final int REASON_MIN_AGE = 10;
    private int pE;

    public PlayFail(int n) {
        this.pE = n;
    }

    @Override
    protected void writeImpl() {
        this.writeC(6);
        this.writeC(this.pE);
    }
}

