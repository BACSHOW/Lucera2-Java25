/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.pfilter;

import java.util.List;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.pfilter.LimitAction;

public class Limit {
    private final List<LimitAction> anF;
    private final boolean anG;
    private final int count;
    private final int anH;

    public Limit(List<LimitAction> list, boolean bl, int n, int n2) {
        this.anF = list;
        this.anG = bl;
        this.count = n;
        this.anH = n2;
    }

    public int getCount() {
        return this.count;
    }

    public int getPerMs() {
        return this.anH;
    }

    public boolean isDropPacket() {
        return this.anG;
    }

    public void enforce(GameClient gameClient) {
        for (LimitAction limitAction : this.anF) {
            limitAction.doIt(gameClient);
        }
    }
}

