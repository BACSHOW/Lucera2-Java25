/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.math.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.util.Rnd;

public class RndSelector<E> {
    private int totalWeight = 0;
    private final List<RndNode<E>> nodes;

    public RndSelector() {
        this.nodes = new ArrayList<RndNode<E>>();
    }

    public RndSelector(int n) {
        this.nodes = new ArrayList<RndNode<E>>(n);
    }

    public void add(E e, int n) {
        if (e == null || n <= 0) {
            return;
        }
        this.totalWeight += n;
        this.nodes.add(new RndNode<E>(e, n));
    }

    public E chance(int n) {
        if (n <= 0) {
            return null;
        }
        Collections.sort(this.nodes);
        int n2 = Rnd.get(n);
        int n3 = 0;
        for (int i = 0; i < this.nodes.size(); ++i) {
            if ((n3 += ((RndNode)this.nodes.get(i)).weight) <= n2) continue;
            return (E)((RndNode)this.nodes.get(i)).value;
        }
        return null;
    }

    public E chance() {
        return this.chance(100);
    }

    public E select() {
        return this.chance(this.totalWeight);
    }

    public void clear() {
        this.totalWeight = 0;
        this.nodes.clear();
    }

    private class RndNode<T>
    implements Comparable<RndNode<T>> {
        private final T value;
        private final int weight;

        public RndNode(T t, int n) {
            this.value = t;
            this.weight = n;
        }

        @Override
        public int compareTo(RndNode<T> rndNode) {
            return this.weight - this.weight;
        }
    }
}

