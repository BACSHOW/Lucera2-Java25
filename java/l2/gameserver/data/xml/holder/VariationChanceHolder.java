/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.item.support.VariationChanceData;
import org.apache.commons.lang3.tuple.Pair;

public class VariationChanceHolder
extends AbstractHolder {
    private static final VariationChanceHolder xa = new VariationChanceHolder();
    private HashMap<Integer, Pair<VariationChanceData, VariationChanceData>> xb = new HashMap();

    public static VariationChanceHolder getInstance() {
        return xa;
    }

    private VariationChanceHolder() {
    }

    @Override
    public int size() {
        return this.xb.size();
    }

    @Override
    public void clear() {
        this.xb.clear();
    }

    public void add(Pair<VariationChanceData, VariationChanceData> pair) {
        if (pair.getLeft() != null && pair.getRight() != null && pair.getLeft().getMineralItemId() == pair.getRight().getMineralItemId()) {
            this.xb.put(pair.getLeft().getMineralItemId(), pair);
        } else if (pair.getLeft() != null) {
            this.xb.put(pair.getLeft().getMineralItemId(), pair);
        } else if (pair.getRight() != null) {
            this.xb.put(pair.getRight().getMineralItemId(), pair);
        } else {
            throw new RuntimeException("Empty mineral");
        }
    }

    public Pair<VariationChanceData, VariationChanceData> getVariationChanceDataForMineral(int n) {
        return this.xb.get(n);
    }
}

