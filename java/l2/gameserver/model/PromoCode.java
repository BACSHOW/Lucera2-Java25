/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.List;
import l2.gameserver.model.promoCode.PromoCodeReward;

public class PromoCode {
    private final String _name;
    private final int KH;
    private final long KI;
    private final long KJ;
    private final List<PromoCodeReward> KK;
    private final boolean KL;
    private final boolean KM;
    private final boolean KN;
    private final int KO;

    public PromoCode(String string, int n, long l, long l2, List<PromoCodeReward> list, boolean bl, boolean bl2, boolean bl3, int n2) {
        this._name = string;
        this.KH = n;
        this.KI = l;
        this.KJ = l2;
        this.KK = list;
        this.KL = bl;
        this.KM = bl2;
        this.KN = bl3;
        this.KO = n2;
    }

    public String getName() {
        return this._name;
    }

    public long getFromDate() {
        return this.KI;
    }

    public long getToDate() {
        return this.KJ;
    }

    public List<PromoCodeReward> getRewards() {
        return this.KK;
    }

    public int getLimit() {
        return this.KH;
    }

    public boolean isLimitByUser() {
        return this.KL;
    }

    public boolean isLimitByHWID() {
        return this.KM;
    }

    public boolean isLimitByIP() {
        return this.KN;
    }

    public int getMinLevel() {
        return this.KO;
    }
}

