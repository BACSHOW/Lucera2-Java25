/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.util;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntObjectHashMap;

public class TroveUtils {
    private static final TIntObjectHashMap te = new TIntObjectHashMapEmpty();
    public static final TIntArrayList EMPTY_INT_ARRAY_LIST = new TIntArrayListEmpty();

    public static <V> TIntObjectHashMap<V> emptyIntObjectMap() {
        return te;
    }

    private static class TIntArrayListEmpty
    extends TIntArrayList {
        TIntArrayListEmpty() {
            super(0);
        }

        @Override
        public void add(int n) {
            throw new UnsupportedOperationException();
        }
    }

    private static class TIntObjectHashMapEmpty<V>
    extends TIntObjectHashMap<V> {
        TIntObjectHashMapEmpty() {
            super(0);
        }

        @Override
        public V put(int n, V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V putIfAbsent(int n, V v) {
            throw new UnsupportedOperationException();
        }
    }
}

