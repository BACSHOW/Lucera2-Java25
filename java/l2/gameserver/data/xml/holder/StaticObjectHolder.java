/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.templates.StaticObjectTemplate;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public final class StaticObjectHolder
extends AbstractHolder {
    private static final StaticObjectHolder wY = new StaticObjectHolder();
    private IntObjectMap<StaticObjectTemplate> wC = new HashIntObjectMap<StaticObjectTemplate>();
    private IntObjectMap<StaticObjectInstance> wZ = new HashIntObjectMap<StaticObjectInstance>();

    public static StaticObjectHolder getInstance() {
        return wY;
    }

    public void addTemplate(StaticObjectTemplate staticObjectTemplate) {
        this.wC.put(staticObjectTemplate.getUId(), staticObjectTemplate);
    }

    public StaticObjectTemplate getTemplate(int n) {
        return this.wC.get(n);
    }

    public void spawnAll() {
        for (StaticObjectTemplate staticObjectTemplate : this.wC.values()) {
            if (!staticObjectTemplate.isSpawn()) continue;
            StaticObjectInstance staticObjectInstance = staticObjectTemplate.newInstance();
            this.wZ.put(staticObjectTemplate.getUId(), staticObjectInstance);
        }
        this.info("spawned: " + this.wZ.size() + " static object(s).");
    }

    public StaticObjectInstance getObject(int n) {
        return this.wZ.get(n);
    }

    @Override
    public int size() {
        return this.wC.size();
    }

    @Override
    public void clear() {
        this.wC.clear();
    }
}

