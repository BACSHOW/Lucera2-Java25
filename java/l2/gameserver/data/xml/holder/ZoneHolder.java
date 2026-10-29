/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.ZoneTemplate;

public class ZoneHolder
extends AbstractHolder {
    private static final ZoneHolder xe = new ZoneHolder();
    private final Map<String, ZoneTemplate> _zones = new HashMap<String, ZoneTemplate>();

    public static ZoneHolder getInstance() {
        return xe;
    }

    public void addTemplate(ZoneTemplate zoneTemplate) {
        this._zones.put(zoneTemplate.getName(), zoneTemplate);
    }

    public ZoneTemplate getTemplate(String string) {
        return this._zones.get(string);
    }

    public Map<String, ZoneTemplate> getZones() {
        return this._zones;
    }

    @Override
    public int size() {
        return this._zones.size();
    }

    @Override
    public void clear() {
        this._zones.clear();
    }
}

