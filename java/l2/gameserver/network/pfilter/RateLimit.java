/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.pfilter;

import l2.commons.util.RateTracker;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.pfilter.Limit;

public class RateLimit {
    private final Limit anU;
    private final RateTracker anV;

    public RateLimit(Limit limit) {
        this.anU = limit;
        this.anV = new RateTracker(limit.getCount(), limit.getPerMs());
    }

    public Limit getLimit() {
        return this.anU;
    }

    public RateTracker getRateTracker() {
        return this.anV;
    }

    public boolean tryPass(GameClient gameClient) {
        if (this.anV.tryPass()) {
            return true;
        }
        this.anU.enforce(gameClient);
        return false;
    }
}

