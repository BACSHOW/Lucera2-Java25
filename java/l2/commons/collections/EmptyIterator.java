/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.collections;

import java.util.Iterator;

public class EmptyIterator<E>
implements Iterator<E> {
    private static final Iterator pK = new EmptyIterator();

    public static <E> Iterator<E> getInstance() {
        return pK;
    }

    private EmptyIterator() {
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

