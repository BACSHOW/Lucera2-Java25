/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.templates.InstantZone;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public class InstantZoneHolder
extends AbstractHolder {
    private static final InstantZoneHolder wg = new InstantZoneHolder();
    private IntObjectMap<InstantZone> wh = new HashIntObjectMap<InstantZone>();

    public static InstantZoneHolder getInstance() {
        return wg;
    }

    public void addInstantZone(InstantZone instantZone) {
        this.wh.put(instantZone.getId(), instantZone);
    }

    public InstantZone getInstantZone(int n) {
        return this.wh.get(n);
    }

    public SchedulingPattern getResetReuseById(int n) {
        InstantZone instantZone = this.getInstantZone(n);
        return instantZone == null ? null : instantZone.getResetReuse();
    }

    public List<Integer> getSharedReuseInstanceIds(int n) {
        if (this.getInstantZone(n).getSharedReuseGroup() < 1) {
            return null;
        }
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (InstantZone instantZone : this.wh.values()) {
            if (instantZone.getSharedReuseGroup() <= 0 || this.getInstantZone(n).getSharedReuseGroup() <= 0 || instantZone.getSharedReuseGroup() != this.getInstantZone(n).getSharedReuseGroup()) continue;
            arrayList.add(instantZone.getId());
        }
        return arrayList;
    }

    public List<Integer> getSharedReuseInstanceIdsByGroup(int n) {
        if (n < 1) {
            return null;
        }
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (InstantZone instantZone : this.wh.values()) {
            if (instantZone.getSharedReuseGroup() <= 0 || instantZone.getSharedReuseGroup() != n) continue;
            arrayList.add(instantZone.getId());
        }
        return arrayList;
    }

    @Override
    public int size() {
        return this.wh.size();
    }

    @Override
    public void clear() {
        this.wh.clear();
    }
}

