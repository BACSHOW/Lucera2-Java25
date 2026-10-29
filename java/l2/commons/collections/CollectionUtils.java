/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    private static <T extends Comparable<T>> void a(List<T> list, int n, int n2) {
        if (n2 - n == 1) {
            if (((Comparable)list.get(n2)).compareTo(list.get(n)) < 0) {
                T comparable = list.get(n);
                list.set(n, list.get(n2));
                list.set(n2, comparable);
            }
        } else if (n2 - n == 2) {
            int n3 = ((Comparable)list.get(n)).compareTo(list.get(n + 1)) < 0 ? n : n + 1;
            int n4 = n3 = ((Comparable)list.get(n3)).compareTo(list.get(n + 2)) < 0 ? n3 : n + 2;
            if (n3 != n) {
                T comparable = list.get(n);
                list.set(n, list.get(n3));
                list.set(n3, comparable);
            }
            CollectionUtils.a(list, n + 1, n2);
        } else if (n2 - n == 3) {
            int n5 = ((Comparable)list.get(n)).compareTo(list.get(n + 1)) < 0 ? n : n + 1;
            n5 = ((Comparable)list.get(n5)).compareTo(list.get(n + 2)) < 0 ? n5 : n + 2;
            int n6 = n5 = ((Comparable)list.get(n5)).compareTo(list.get(n + 3)) < 0 ? n5 : n + 3;
            if (n5 != n) {
                T comparable = list.get(n);
                list.set(n, list.get(n5));
                list.set(n5, comparable);
            }
            int n7 = ((Comparable)list.get(n2)).compareTo(list.get(n2 - 1)) > 0 ? n2 : n2 - 1;
            int n8 = n7 = ((Comparable)list.get(n7)).compareTo(list.get(n2 - 2)) > 0 ? n7 : n2 - 2;
            if (n7 != n2) {
                T comparable = list.get(n2);
                list.set(n2, list.get(n7));
                list.set(n7, comparable);
            }
            CollectionUtils.a(list, n + 1, n2 - 1);
        }
    }

    private static <T extends Comparable<T>> void b(List<T> list, int n, int n2) {
        int n3 = n2;
        int n4 = n;
        if (n3 - n4 <= 3) {
            CollectionUtils.a(list, n4, n3);
            return;
        }
        T comparable = list.get((n4 + n3) / 2);
        list.set((n4 + n3) / 2, list.get(n3));
        list.set(n3, comparable);
        while (n4 < n3) {
            while (((Comparable)list.get(n4)).compareTo(comparable) <= 0 && n4 < n3) {
                ++n4;
            }
            while (comparable.compareTo(list.get(n3)) <= 0 && n4 < n3) {
                --n3;
            }
            if (n4 >= n3) continue;
            T comparable2 = list.get(n4);
            list.set(n4, list.get(n3));
            list.set(n3, comparable2);
        }
        list.set(n2, list.get(n3));
        list.set(n3, comparable);
        CollectionUtils.b(list, n, n4 - 1);
        CollectionUtils.b(list, n3 + 1, n2);
    }

    public static <T extends Comparable<T>> void eqSort(List<T> list) {
        CollectionUtils.b(list, 0, list.size() - 1);
    }

    private static <T> void a(List<T> list, int n, int n2, Comparator<? super T> comparator) {
        if (n2 - n == 1) {
            if (comparator.compare(list.get(n2), list.get(n)) < 0) {
                T t = list.get(n);
                list.set(n, list.get(n2));
                list.set(n2, t);
            }
        } else if (n2 - n == 2) {
            int n3 = comparator.compare(list.get(n), list.get(n + 1)) < 0 ? n : n + 1;
            int n4 = n3 = comparator.compare(list.get(n3), list.get(n + 2)) < 0 ? n3 : n + 2;
            if (n3 != n) {
                T t = list.get(n);
                list.set(n, list.get(n3));
                list.set(n3, t);
            }
            CollectionUtils.a(list, n + 1, n2, comparator);
        } else if (n2 - n == 3) {
            int n5 = comparator.compare(list.get(n), list.get(n + 1)) < 0 ? n : n + 1;
            n5 = comparator.compare(list.get(n5), list.get(n + 2)) < 0 ? n5 : n + 2;
            int n6 = n5 = comparator.compare(list.get(n5), list.get(n + 3)) < 0 ? n5 : n + 3;
            if (n5 != n) {
                T t = list.get(n);
                list.set(n, list.get(n5));
                list.set(n5, t);
            }
            int n7 = comparator.compare(list.get(n2), list.get(n2 - 1)) > 0 ? n2 : n2 - 1;
            int n8 = n7 = comparator.compare(list.get(n7), list.get(n2 - 2)) > 0 ? n7 : n2 - 2;
            if (n7 != n2) {
                T t = list.get(n2);
                list.set(n2, list.get(n7));
                list.set(n7, t);
            }
            CollectionUtils.a(list, n + 1, n2 - 1, comparator);
        }
    }

    private static <T> void b(List<T> list, int n, int n2, Comparator<? super T> comparator) {
        int n3 = n2;
        int n4 = n;
        if (n3 - n4 <= 3) {
            CollectionUtils.a(list, n4, n3, comparator);
            return;
        }
        T t = list.get((n4 + n3) / 2);
        list.set((n4 + n3) / 2, list.get(n3));
        list.set(n3, t);
        while (n4 < n3) {
            while (comparator.compare(list.get(n4), t) <= 0 && n4 < n3) {
                ++n4;
            }
            while (comparator.compare(t, list.get(n3)) <= 0 && n4 < n3) {
                --n3;
            }
            if (n4 >= n3) continue;
            T t2 = list.get(n4);
            list.set(n4, list.get(n3));
            list.set(n3, t2);
        }
        list.set(n2, list.get(n3));
        list.set(n3, t);
        CollectionUtils.b(list, n, n4 - 1, comparator);
        CollectionUtils.b(list, n3 + 1, n2, comparator);
    }

    public static <T> void eqSort(List<T> list, Comparator<? super T> comparator) {
        CollectionUtils.b(list, 0, list.size() - 1, comparator);
    }

    public static <T extends Comparable<T>> void insertionSort(List<T> list) {
        for (int i = 1; i < list.size(); ++i) {
            T comparable;
            int n;
            T comparable2 = list.get(i);
            for (n = i; n > 0 && (comparable = list.get(n - 1)).compareTo(comparable2) > 0; --n) {
                list.set(n, comparable);
            }
            list.set(n, comparable2);
        }
    }

    public static <T> void insertionSort(List<T> list, Comparator<? super T> comparator) {
        for (int i = 1; i < list.size(); ++i) {
            T t;
            int n;
            T t2 = list.get(i);
            for (n = i; n > 0 && comparator.compare(t = list.get(n - 1), t2) > 0; --n) {
                list.set(n, t);
            }
            list.set(n, t2);
        }
    }

    public static <E> int hashCode(Collection<E> collection) {
        int n = 1;
        for (E e : collection) {
            n = 31 * n + (e == null ? 0 : e.hashCode());
        }
        return n;
    }

    public static <E> E safeGet(List<E> list, int n) {
        return list.size() > n ? (E)list.get(n) : null;
    }

    public static <E> Collection<E> join(Collection<E> ... collectionArray) {
        LinkedList<E> linkedList = new LinkedList<E>();
        for (Collection<E> collection : collectionArray) {
            linkedList.addAll(collection);
        }
        return linkedList;
    }

    public static <E> List<E> join(List<E> ... listArray) {
        LinkedList<E> linkedList = new LinkedList<E>();
        for (List<E> list : listArray) {
            linkedList.addAll(list);
        }
        return linkedList;
    }
}

