/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.DoorTemplate;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public final class DoorHolder
extends AbstractHolder {
    private static final DoorHolder vQ = new DoorHolder();
    private IntObjectMap<DoorTemplate> _doors = new HashIntObjectMap<DoorTemplate>();

    public static DoorHolder getInstance() {
        return vQ;
    }

    public void addTemplate(DoorTemplate doorTemplate) {
        this._doors.put(doorTemplate.getNpcId(), doorTemplate);
    }

    public DoorTemplate getTemplate(int n) {
        return this._doors.get(n);
    }

    public IntObjectMap<DoorTemplate> getDoors() {
        return this._doors;
    }

    @Override
    public int size() {
        return this._doors.size();
    }

    @Override
    public void clear() {
        this._doors.clear();
    }
}

