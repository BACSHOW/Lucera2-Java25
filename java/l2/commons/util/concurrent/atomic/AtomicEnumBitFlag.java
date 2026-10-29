/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.util.concurrent.atomic;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicEnumBitFlag<E extends Enum<E>> {
    private final AtomicLong tf = new AtomicLong();

    public boolean set(E e, boolean bl) {
        long l;
        long l2;
        int n = ((Enum)e).ordinal();
        if (n > 63) {
            throw new IllegalArgumentException("Maxium 64 enum values allowed");
        }
        long l3 = 1L << n;
        while (!this.tf.compareAndSet(l2 = this.tf.get(), l = bl ? l2 | l3 : l2 & (l3 ^ 0xFFFFFFFFFFFFFFFFL))) {
        }
        return (l2 & l3) != 0L;
    }

    public boolean get(E e) {
        int n = ((Enum)e).ordinal();
        if (n > 63) {
            throw new IllegalArgumentException("Maxium 64 enum values allowed");
        }
        long l = 1L << n;
        return (this.tf.get() & l) != 0L;
    }
}

