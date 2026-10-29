/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.math;

public class SafeMath {
    public static int addAndCheck(int n, int n2) throws ArithmeticException {
        return SafeMath.a(n, n2, "overflow: add", false);
    }

    public static int addAndLimit(int n, int n2) {
        return SafeMath.a(n, n2, null, true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static int a(int n, int n2, String string, boolean bl) {
        if (n > n2) {
            return SafeMath.a(n2, n, string, bl);
        }
        if (n < 0) {
            if (n2 >= 0) return n + n2;
            if (Integer.MIN_VALUE - n2 <= n) {
                return n + n2;
            }
            if (!bl) throw new ArithmeticException(string);
            return Integer.MIN_VALUE;
        }
        if (n <= Integer.MAX_VALUE - n2) {
            return n + n2;
        }
        if (!bl) throw new ArithmeticException(string);
        return Integer.MAX_VALUE;
    }

    public static long addAndLimit(long l, long l2) {
        return SafeMath.a(l, l2, "overflow: add", true);
    }

    public static long addAndCheck(long l, long l2) throws ArithmeticException {
        return SafeMath.a(l, l2, "overflow: add", false);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static long a(long l, long l2, String string, boolean bl) {
        if (l > l2) {
            return SafeMath.a(l2, l, string, bl);
        }
        if (l < 0L) {
            if (l2 >= 0L) return l + l2;
            if (Long.MIN_VALUE - l2 <= l) {
                return l + l2;
            }
            if (!bl) throw new ArithmeticException(string);
            return Long.MIN_VALUE;
        }
        if (l <= Long.MAX_VALUE - l2) {
            return l + l2;
        }
        if (!bl) throw new ArithmeticException(string);
        return Long.MAX_VALUE;
    }

    public static int mulAndCheck(int n, int n2) throws ArithmeticException {
        return SafeMath.b(n, n2, "overflow: mul", false);
    }

    public static int mulAndLimit(int n, int n2) {
        return SafeMath.b(n, n2, "overflow: mul", true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static int b(int n, int n2, String string, boolean bl) {
        if (n > n2) {
            return SafeMath.b(n2, n, string, bl);
        }
        if (n < 0) {
            if (n2 < 0) {
                if (n >= Integer.MAX_VALUE / n2) {
                    return n * n2;
                }
                if (!bl) throw new ArithmeticException(string);
                return Integer.MAX_VALUE;
            }
            if (n2 <= 0) return 0;
            if (Integer.MIN_VALUE / n2 <= n) {
                return n * n2;
            }
            if (!bl) throw new ArithmeticException(string);
            return Integer.MIN_VALUE;
        }
        if (n <= 0) return 0;
        if (n <= Integer.MAX_VALUE / n2) {
            return n * n2;
        }
        if (!bl) throw new ArithmeticException(string);
        return Integer.MAX_VALUE;
    }

    public static long mulAndCheck(long l, long l2) throws ArithmeticException {
        return SafeMath.b(l, l2, "overflow: mul", false);
    }

    public static long mulAndLimit(long l, long l2) {
        return SafeMath.b(l, l2, "overflow: mul", true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static long b(long l, long l2, String string, boolean bl) {
        if (l > l2) {
            return SafeMath.b(l2, l, string, bl);
        }
        if (l < 0L) {
            if (l2 < 0L) {
                if (l >= Long.MAX_VALUE / l2) {
                    return l * l2;
                }
                if (!bl) throw new ArithmeticException(string);
                return Long.MAX_VALUE;
            }
            if (l2 <= 0L) return 0L;
            if (Long.MIN_VALUE / l2 <= l) {
                return l * l2;
            }
            if (!bl) throw new ArithmeticException(string);
            return Long.MIN_VALUE;
        }
        if (l <= 0L) return 0L;
        if (l <= Long.MAX_VALUE / l2) {
            return l * l2;
        }
        if (!bl) throw new ArithmeticException(string);
        return Long.MAX_VALUE;
    }
}

