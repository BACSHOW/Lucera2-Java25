/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import gnu.trove.TIntObjectHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.gameserver.data.xml.holder.DoorHolder;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.data.xml.holder.ZoneHolder;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.utils.Location;

public class ReflectionManager {
    public static final Reflection DEFAULT = Reflection.createReflection(0);
    public static final Reflection GIRAN_HARBOR = Reflection.createReflection(-1);
    public static final Reflection JAIL = Reflection.createReflection(-2);
    private static final ReflectionManager AY = new ReflectionManager();
    private final TIntObjectHashMap<Reflection> AZ = new TIntObjectHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = this.lock.readLock();
    private final Lock writeLock = this.lock.writeLock();

    public static ReflectionManager getInstance() {
        return AY;
    }

    private ReflectionManager() {
        this.add(DEFAULT);
        this.add(GIRAN_HARBOR);
        this.add(JAIL);
        DEFAULT.init(DoorHolder.getInstance().getDoors(), ZoneHolder.getInstance().getZones());
        GIRAN_HARBOR.fillSpawns(InstantZoneHolder.getInstance().getInstantZone(10).getSpawnsInfo());
        JAIL.setCoreLoc(new Location(-114648, -249384, -2984));
    }

    public Reflection get(int n) {
        this.readLock.lock();
        try {
            Reflection reflection = this.AZ.get(n);
            return reflection;
        }
        finally {
            this.readLock.unlock();
        }
    }

    public Reflection add(Reflection reflection) {
        this.writeLock.lock();
        try {
            Reflection reflection2 = this.AZ.put(reflection.getId(), reflection);
            return reflection2;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public Reflection remove(Reflection reflection) {
        this.writeLock.lock();
        try {
            Reflection reflection2 = this.AZ.remove(reflection.getId());
            return reflection2;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public Reflection[] getAll() {
        this.readLock.lock();
        try {
            Reflection[] reflectionArray = this.AZ.getValues(new Reflection[this.AZ.size()]);
            return reflectionArray;
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCountByIzId(int n) {
        this.readLock.lock();
        try {
            int n2 = 0;
            for (Reflection reflection : this.getAll()) {
                if (reflection.getInstancedZoneId() != n) continue;
                ++n2;
            }
            int n3 = n2;
            return n3;
        }
        finally {
            this.readLock.unlock();
        }
    }

    public int size() {
        return this.AZ.size();
    }
}

