/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.lang;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;

public final class ArrayUtils {
    public static final int INDEX_NOT_FOUND = -1;

    public static <T> T valid(T[] TArray, int n) {
        if (TArray == null) {
            return null;
        }
        if (n < 0 || TArray.length <= n) {
            return null;
        }
        return TArray[n];
    }

    public static <T> T[] add(T[] TArray, T t) {
        Class<? extends T> clazz = (Class<? extends T>)(TArray != null ? TArray.getClass().getComponentType() : (t != null ? t.getClass() : Object.class));
        T[] TArray2 = ArrayUtils.a(TArray, clazz);
        TArray2[TArray2.length - 1] = t;
        return TArray2;
    }

    private static <T> T[] a(T[] TArray, Class<? extends T> clazz) {
        if (TArray != null) {
            int n = Array.getLength(TArray);
            Object[] objectArray = (Object[])Array.newInstance(TArray.getClass().getComponentType(), n + 1);
            System.arraycopy(TArray, 0, objectArray, 0, n);
            return (T[])objectArray;
        }
        return (T[])Array.newInstance(clazz, 1);
    }

    public static <T> boolean contains(T[] TArray, T t) {
        if (TArray == null) {
            return false;
        }
        for (int i = 0; i < TArray.length; ++i) {
            if (t != TArray[i]) continue;
            return true;
        }
        return false;
    }

    public static <T> int indexOf(T[] TArray, T t, int n) {
        if (n < 0 || TArray.length <= n) {
            return -1;
        }
        for (int i = n; i < TArray.length; ++i) {
            if (t != TArray[i]) continue;
            return i;
        }
        return -1;
    }

    public static <T> T[] remove(T[] TArray, T t) {
        if (TArray == null) {
            return null;
        }
        int n = ArrayUtils.indexOf(TArray, t, 0);
        if (n == -1) {
            return TArray;
        }
        int n2 = TArray.length;
        Object[] objectArray = (Object[])Array.newInstance(TArray.getClass().getComponentType(), n2 - 1);
        System.arraycopy(TArray, 0, objectArray, 0, n);
        if (n < n2 - 1) {
            System.arraycopy(TArray, n + 1, objectArray, n, n2 - n - 1);
        }
        return (T[])objectArray;
    }

    private static <T extends Comparable<T>> void a(T[] TArray, int n, int n2) {
        if (n2 - n == 1) {
            if (TArray[n2].compareTo(TArray[n]) < 0) {
                T t = TArray[n];
                TArray[n] = TArray[n2];
                TArray[n2] = t;
            }
        } else if (n2 - n == 2) {
            int n3 = TArray[n].compareTo(TArray[n + 1]) < 0 ? n : n + 1;
            int n4 = n3 = TArray[n3].compareTo(TArray[n + 2]) < 0 ? n3 : n + 2;
            if (n3 != n) {
                T t = TArray[n];
                TArray[n] = TArray[n3];
                TArray[n3] = t;
            }
            ArrayUtils.a(TArray, (int)(n + 1), (int)n2);
        } else if (n2 - n == 3) {
            int n5 = TArray[n].compareTo(TArray[n + 1]) < 0 ? n : n + 1;
            n5 = TArray[n5].compareTo(TArray[n + 2]) < 0 ? n5 : n + 2;
            int n6 = n5 = TArray[n5].compareTo(TArray[n + 3]) < 0 ? n5 : n + 3;
            if (n5 != n) {
                T t = TArray[n];
                TArray[n] = TArray[n5];
                TArray[n5] = t;
            }
            int n7 = TArray[n2].compareTo(TArray[n2 - 1]) > 0 ? n2 : n2 - 1;
            int n8 = n7 = TArray[n7].compareTo(TArray[n2 - 2]) > 0 ? n7 : n2 - 2;
            if (n7 != n2) {
                T t = TArray[n2];
                TArray[n2] = TArray[n7];
                TArray[n7] = t;
            }
            ArrayUtils.a(TArray, (int)(n + 1), (int)(n2 - 1));
        }
    }

    private static <T extends Comparable<T>> void b(T[] TArray, int n, int n2) {
        int n3 = n2;
        int n4 = n;
        if (n3 - n4 <= 3) {
            ArrayUtils.a(TArray, (int)n4, (int)n3);
            return;
        }
        T t = TArray[(n4 + n3) / 2];
        TArray[(n4 + n3) / 2] = TArray[n3];
        TArray[n3] = t;
        while (n4 < n3) {
            while (TArray[n4].compareTo(t) <= 0 && n4 < n3) {
                ++n4;
            }
            while (t.compareTo(TArray[n3]) <= 0 && n4 < n3) {
                --n3;
            }
            if (n4 >= n3) continue;
            T t2 = TArray[n4];
            TArray[n4] = TArray[n3];
            TArray[n3] = t2;
        }
        TArray[n2] = TArray[n3];
        TArray[n3] = t;
        ArrayUtils.b(TArray, (int)n, (int)(n4 - 1));
        ArrayUtils.b(TArray, (int)(n3 + 1), (int)n2);
    }

    public static <T extends Comparable<T>> void eqSort(T[] TArray) {
        ArrayUtils.b(TArray, (int)0, (int)(TArray.length - 1));
    }

    private static <T> void a(T[] TArray, int n, int n2, Comparator<T> comparator) {
        if (n2 - n == 1) {
            if (comparator.compare(TArray[n2], TArray[n]) < 0) {
                T t = TArray[n];
                TArray[n] = TArray[n2];
                TArray[n2] = t;
            }
        } else if (n2 - n == 2) {
            int n3 = comparator.compare(TArray[n], TArray[n + 1]) < 0 ? n : n + 1;
            int n4 = n3 = comparator.compare(TArray[n3], TArray[n + 2]) < 0 ? n3 : n + 2;
            if (n3 != n) {
                T t = TArray[n];
                TArray[n] = TArray[n3];
                TArray[n3] = t;
            }
            ArrayUtils.a(TArray, n + 1, n2, comparator);
        } else if (n2 - n == 3) {
            int n5 = comparator.compare(TArray[n], TArray[n + 1]) < 0 ? n : n + 1;
            n5 = comparator.compare(TArray[n5], TArray[n + 2]) < 0 ? n5 : n + 2;
            int n6 = n5 = comparator.compare(TArray[n5], TArray[n + 3]) < 0 ? n5 : n + 3;
            if (n5 != n) {
                T t = TArray[n];
                TArray[n] = TArray[n5];
                TArray[n5] = t;
            }
            int n7 = comparator.compare(TArray[n2], TArray[n2 - 1]) > 0 ? n2 : n2 - 1;
            int n8 = n7 = comparator.compare(TArray[n7], TArray[n2 - 2]) > 0 ? n7 : n2 - 2;
            if (n7 != n2) {
                T t = TArray[n2];
                TArray[n2] = TArray[n7];
                TArray[n7] = t;
            }
            ArrayUtils.a(TArray, n + 1, n2 - 1, comparator);
        }
    }

    private static <T> void b(T[] TArray, int n, int n2, Comparator<T> comparator) {
        int n3 = n2;
        int n4 = n;
        if (n3 - n4 <= 3) {
            ArrayUtils.a(TArray, n4, n3, comparator);
            return;
        }
        T t = TArray[(n4 + n3) / 2];
        TArray[(n4 + n3) / 2] = TArray[n3];
        TArray[n3] = t;
        while (n4 < n3) {
            while (comparator.compare(TArray[n4], t) <= 0 && n4 < n3) {
                ++n4;
            }
            while (comparator.compare(t, TArray[n3]) <= 0 && n4 < n3) {
                --n3;
            }
            if (n4 >= n3) continue;
            T t2 = TArray[n4];
            TArray[n4] = TArray[n3];
            TArray[n3] = t2;
        }
        TArray[n2] = TArray[n3];
        TArray[n3] = t;
        ArrayUtils.b(TArray, n, n4 - 1, comparator);
        ArrayUtils.b(TArray, n3 + 1, n2, comparator);
    }

    public static <T> void eqSort(T[] TArray, Comparator<T> comparator) {
        ArrayUtils.b(TArray, 0, TArray.length - 1, comparator);
    }

    public static int[] toArray(Collection<Integer> collection) {
        int[] nArray = new int[collection.size()];
        int n = 0;
        for (Integer n2 : collection) {
            nArray[n++] = n2;
        }
        return nArray;
    }

    public static int[] createAscendingArray(int n, int n2) {
        int n3 = n2 - n;
        int[] nArray = new int[n3 + 1];
        int n4 = 0;
        int n5 = n;
        while (n5 <= n2) {
            nArray[n4] = n5++;
            ++n4;
        }
        return nArray;
    }
}

