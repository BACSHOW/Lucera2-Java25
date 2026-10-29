/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;

public final class KarmaIncreaseDataHolder
extends AbstractHolder {
    private static final KarmaIncreaseDataHolder wl = new KarmaIncreaseDataHolder();
    private final Map<Integer, Double> wm = new HashMap<Integer, Double>();

    public static KarmaIncreaseDataHolder getInstance() {
        return wl;
    }

    public void addData(int n, double d) {
        this.wm.put(n, d);
    }

    public double getData(int n) {
        return this.wm.get(n);
    }

    @Override
    public int size() {
        return this.wm.size();
    }

    @Override
    public void clear() {
        this.wm.clear();
    }
}

