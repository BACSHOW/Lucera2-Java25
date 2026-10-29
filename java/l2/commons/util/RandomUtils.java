/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.util;

import java.util.Collection;
import java.util.Comparator;
import l2.commons.util.Rnd;
import org.apache.commons.lang3.tuple.Pair;

public class RandomUtils {
    public static final Comparator<Pair<?, Double>> DOUBLE_GROUP_COMPARATOR = new Comparator<Pair<?, Double>>(){

        @Override
        public int compare(Pair<?, Double> pair, Pair<?, Double> pair2) {
            double d = pair.getRight() - pair2.getRight();
            return d > 0.0 ? 1 : (d < 0.0 ? -1 : 0);
        }
    };

    public static <G> G pickRandomSortedGroup(Collection<Pair<G, Double>> collection, double d) {
        double d2 = d * Rnd.get();
        double d3 = 0.0;
        for (Pair<G, Double> pair : collection) {
            if (!(d2 <= (d3 += pair.getRight().doubleValue()))) continue;
            return pair.getLeft();
        }
        return null;
    }
}

