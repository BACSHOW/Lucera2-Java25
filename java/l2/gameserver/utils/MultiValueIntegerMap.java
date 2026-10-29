/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiValueIntegerMap {
    private Map<Integer, List<Integer>> map = new ConcurrentHashMap<Integer, List<Integer>>();

    public Set<Integer> keySet() {
        return this.map.keySet();
    }

    public Collection<List<Integer>> values() {
        return this.map.values();
    }

    public List<Integer> allValues() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (Map.Entry<Integer, List<Integer>> entry : this.map.entrySet()) {
            arrayList.addAll((Collection<Integer>)entry.getValue());
        }
        return arrayList;
    }

    public Set<Map.Entry<Integer, List<Integer>>> entrySet() {
        return this.map.entrySet();
    }

    public List<Integer> remove(Integer n) {
        return this.map.remove(n);
    }

    public List<Integer> get(Integer n) {
        return this.map.get(n);
    }

    public boolean containsKey(Integer n) {
        return this.map.containsKey(n);
    }

    public void clear() {
        this.map.clear();
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Integer remove(Integer n, Integer n2) {
        List<Integer> list = this.map.get(n);
        if (list == null) {
            return null;
        }
        boolean bl = list.remove(n2);
        if (!bl) {
            return null;
        }
        if (list.isEmpty()) {
            this.remove(n);
        }
        return n2;
    }

    public Integer removeValue(Integer n) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>(1);
        for (Map.Entry<Integer, List<Integer>> object : this.map.entrySet()) {
            object.getValue().remove(n);
            if (!object.getValue().isEmpty()) continue;
            arrayList.add(object.getKey());
        }
        for (Integer n2 : arrayList) {
            this.remove(n2);
        }
        return n;
    }

    public Integer put(Integer n, Integer n2) {
        List<Integer> list = this.map.get(n);
        if (list == null) {
            list = new CopyOnWriteArrayList<Integer>();
            this.map.put(n, list);
        }
        list.add(n2);
        return n2;
    }

    public boolean containsValue(Integer n) {
        for (Map.Entry<Integer, List<Integer>> entry : this.map.entrySet()) {
            if (!entry.getValue().contains(n)) continue;
            return true;
        }
        return false;
    }

    public boolean containsValue(Integer n, Integer n2) {
        List<Integer> list = this.map.get(n);
        if (list == null) {
            return false;
        }
        return list.contains(n2);
    }

    public int size(Integer n) {
        List<Integer> list = this.map.get(n);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public boolean putAll(Integer n, Collection<? extends Integer> collection) {
        if (collection == null || collection.size() == 0) {
            return false;
        }
        boolean bl = false;
        List<Integer> list = this.map.get(n);
        if (list == null) {
            list = new CopyOnWriteArrayList<Integer>();
            list.addAll(collection);
            if (list.size() > 0) {
                this.map.put(n, list);
                bl = true;
            }
        } else {
            bl = list.addAll(collection);
        }
        return bl;
    }

    public int totalSize() {
        int n = 0;
        for (Map.Entry<Integer, List<Integer>> entry : this.map.entrySet()) {
            n += entry.getValue().size();
        }
        return n;
    }
}

