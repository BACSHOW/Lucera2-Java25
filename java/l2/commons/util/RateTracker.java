/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.util;

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateTracker {
    private static final Logger sQ = LoggerFactory.getLogger(RateTracker.class);
    public static final int MAX_COUNT = 15000;
    public static final int MIN_COUNT = 0;
    public static final int MAX_DURATION = 250000;
    public static final int MIN_DURATION = 10;
    private static final long sR = -1125899906842624L;
    private static final int sS = 50;
    private static final long sT = 0x3FFFF00000000L;
    private static final int sU = 32;
    private static final long sV = 0xFFFC0000L;
    private static final int sW = 18;
    private static final long sX = 262143L;
    private static final int sY = 0;
    private final long sZ = System.currentTimeMillis();
    private final int limit;
    private final int duration;
    private final boolean ta;
    private final AtomicLong tb = new AtomicLong(0L);

    public RateTracker(int n, int n2, boolean bl) {
        this.limit = n;
        this.duration = n2;
        this.ta = bl;
    }

    public RateTracker(int n, int n2) {
        this(n, n2, false);
    }

    private static final int f(long l) {
        return (int)((l & 0x3FFFFL) >> 0);
    }

    private static final int g(long l) {
        return (int)((l & 0xFFFC0000L) >> 18);
    }

    private static final int h(long l) {
        return (int)((l & 0x3FFFF00000000L) >> 32);
    }

    private static final int i(long l) {
        return (int)((l & 0xFFFC000000000000L) >> 50);
    }

    private static final long a(int n, int n2, int n3, int n4) {
        return (long)n << 0 & 0x3FFFFL | (long)n2 << 18 & 0xFFFC0000L | (long)n3 << 32 & 0x3FFFF00000000L | (long)n4 << 50 & 0xFFFC000000000000L;
    }

    private final int ba() {
        return (int)(System.currentTimeMillis() - this.sZ);
    }

    public boolean tryPass() {
        return this.tryPass(1);
    }

    public boolean tryPass(int n) {
        int n2;
        int n3;
        int n4;
        int n5;
        long l;
        do {
            int n6 = this.ba();
            l = this.tb.get();
            int n7 = RateTracker.f(l);
            n4 = RateTracker.g(l);
            n5 = n6 / this.duration;
            if (n5 != n7) {
                long l2 = RateTracker.a(n5, 0, n7, n4);
                this.tb.compareAndSet(l, l2);
                l = l2;
                n7 = n5;
                n4 = 0;
            }
            int n8 = n4;
            int n9 = n6 - n5 * this.duration;
            float f = (float)n9 / (float)this.duration;
            n3 = RateTracker.h(l);
            n2 = RateTracker.i(l);
            if (n3 + 1 == n7) {
                n8 += Math.round((float)n2 * f);
            }
            if (n8 + n <= this.limit) continue;
            if (this.ta) {
                this.tb.compareAndSet(l, RateTracker.a(n5, n4 + n, n3, n2));
            }
            return false;
        } while (!this.tb.compareAndSet(l, RateTracker.a(n5, n4 + n, n3, n2)));
        return true;
    }
}

