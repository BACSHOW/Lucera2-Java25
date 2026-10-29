/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.collections;

import java.util.Iterator;
import java.util.List;
import l2.commons.collections.EmptyIterator;

public class JoinedIterator<E>
implements Iterator<E> {
    private Iterator<E>[] pL;
    private int pM;
    private Iterator<E> pN;
    private Iterator<E> pO;

    public JoinedIterator(List<Iterator<E>> list) {
        this(list.toArray(new Iterator[list.size()]));
    }

    public JoinedIterator(Iterator ... iteratorArray) {
        if (iteratorArray == null) {
            throw new NullPointerException("Unexpected NULL iterators argument");
        }
        this.pL = iteratorArray;
    }

    @Override
    public boolean hasNext() {
        this.updateCurrentIterator();
        return this.pN.hasNext();
    }

    @Override
    public E next() {
        this.updateCurrentIterator();
        return this.pN.next();
    }

    @Override
    public void remove() {
        this.updateCurrentIterator();
        this.pO.remove();
    }

    protected void updateCurrentIterator() {
        if (this.pN == null) {
            this.pN = this.pL.length == 0 ? EmptyIterator.getInstance() : this.pL[0];
            this.pO = this.pN;
        }
        while (!this.pN.hasNext() && this.pM < this.pL.length - 1) {
            ++this.pM;
            this.pN = this.pL[this.pM];
        }
    }
}

