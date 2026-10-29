/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.random.RandomGenerator;

public class Rnd {
    private static final ThreadLocal<RandomGenerator> tc = new ThreadLocalGeneratorHolder();
    private static AtomicLong td = new AtomicLong(8682522807148012L);

    private Rnd() {
    }

    private static RandomGenerator bb() {
        return tc.get();
    }

    public static double get() {
        return Rnd.bb().nextDouble();
    }

    public static int get(int n) {
        return Rnd.bb().nextInt(n);
    }

    public static long get(long l) {
        return (long)(Rnd.bb().nextDouble() * (double)l);
    }

    public static int get(int n, int n2) {
        return n + Rnd.get(n2 - n + 1);
    }

    public static long get(long l, long l2) {
        return l + Rnd.get(l2 - l + 1L);
    }

    public static int nextInt() {
        return Rnd.bb().nextInt();
    }

    public static double nextDouble() {
        return Rnd.bb().nextDouble();
    }

    public static double nextGaussian() {
        return Rnd.bb().nextGaussian();
    }

    public static boolean nextBoolean() {
        return Rnd.bb().nextBoolean();
    }

    public static boolean chance(int n) {
        return n >= 1 && (n > 99 || Rnd.bb().nextInt(99) + 1 <= n);
    }

    public static boolean chance(double d) {
        return Rnd.bb().nextDouble() <= d / 100.0;
    }

    public static <E> E get(E[] EArray) {
        return EArray[Rnd.get(EArray.length)];
    }

    public static int get(int[] nArray) {
        return nArray[Rnd.get(nArray.length)];
    }

    public static <E> E get(List<E> list) {
        return list.get(Rnd.get(list.size()));
    }

    static final class ThreadLocalGeneratorHolder
    extends ThreadLocal<RandomGenerator> {
        ThreadLocalGeneratorHolder() {
        }

        @Override
        public RandomGenerator initialValue() {
            return new MersenneTwister(td.getAndIncrement() + System.nanoTime());
        }
    }
}

