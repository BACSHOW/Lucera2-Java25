/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2.s2c;

import l2.authserver.network.l2.s2c.L2LoginServerPacket;

public final class AccountKicked
extends L2LoginServerPacket {
    private int oH;

    public AccountKicked(AccountKickedReason accountKickedReason) {
        this.oH = accountKickedReason.getCode();
    }

    @Override
    protected void writeImpl() {
        this.writeC(2);
        this.writeD(this.oH);
    }

    public static enum AccountKickedReason {
        REASON_FALSE_DATA_STEALER_REPORT(0),
        REASON_DATA_STEALER(1),
        REASON_SOUSPICION_DATA_STEALER(3),
        REASON_NON_PAYEMENT_CELL_PHONE(4),
        REASON_30_DAYS_SUSPENDED_CASH(8),
        REASON_PERMANENTLY_SUSPENDED_CASH(16),
        REASON_PERMANENTLY_BANNED(32),
        REASON_ACCOUNT_MUST_BE_VERIFIED(64);

        private final int pw;

        private AccountKickedReason(int n2) {
            this.pw = n2;
        }

        public final int getCode() {
            return this.pw;
        }
    }
}

