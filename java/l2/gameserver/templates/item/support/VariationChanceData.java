/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item.support;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class VariationChanceData {
    private final int avz;
    private final List<Pair<List<Pair<Integer, Double>>, Double>> avA;
    private final List<Pair<List<Pair<Integer, Double>>, Double>> avB;

    public VariationChanceData(int n, List<Pair<List<Pair<Integer, Double>>, Double>> list, List<Pair<List<Pair<Integer, Double>>, Double>> list2) {
        this.avz = n;
        this.avA = list;
        this.avB = list2;
    }

    public int getMineralItemId() {
        return this.avz;
    }

    public List<Pair<List<Pair<Integer, Double>>, Double>> getVariation1() {
        return this.avA;
    }

    public List<Pair<List<Pair<Integer, Double>>, Double>> getVariation2() {
        return this.avB;
    }
}

