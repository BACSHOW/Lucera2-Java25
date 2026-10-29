/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.templates.StatsSet;

public class ClassLevelGainHolder
extends AbstractHolder {
    private static final ClassLevelGainHolder vM = new ClassLevelGainHolder();
    private final Map<ClassId, Map<Integer, StatsSet>> vN = new HashMap<ClassId, Map<Integer, StatsSet>>();

    public static ClassLevelGainHolder getInstance() {
        return vM;
    }

    public void addLevelGain(ClassId classId2, int n, StatsSet statsSet) {
        Map map = this.vN.computeIfAbsent(classId2, classId -> new LinkedHashMap());
        map.put(n, statsSet);
    }

    public Double getHp(ClassId classId, int n) {
        Map<Integer, StatsSet> map = this.vN.get((Object)classId);
        if (map == null) {
            return null;
        }
        StatsSet statsSet = map.get(n);
        if (statsSet == null) {
            return null;
        }
        return statsSet.getDouble("hp");
    }

    public Double getCp(ClassId classId, int n) {
        Map<Integer, StatsSet> map = this.vN.get((Object)classId);
        if (map == null) {
            return null;
        }
        StatsSet statsSet = map.get(n);
        if (statsSet == null) {
            return null;
        }
        return statsSet.getDouble("cp");
    }

    public Double getMp(ClassId classId, int n) {
        Map<Integer, StatsSet> map = this.vN.get((Object)classId);
        if (map == null) {
            return null;
        }
        StatsSet statsSet = map.get(n);
        if (statsSet == null) {
            return null;
        }
        return statsSet.getDouble("mp");
    }

    @Override
    public int size() {
        return this.vN.size();
    }

    @Override
    public void clear() {
        this.vN.clear();
    }
}

