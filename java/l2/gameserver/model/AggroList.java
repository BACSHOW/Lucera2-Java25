/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.commons.collections.LazyArrayList;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;

public class AggroList {
    private final NpcInstance CF;
    private final TIntObjectHashMap<AggroInfo> CG = new TIntObjectHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = this.lock.readLock();
    private final Lock writeLock = this.lock.writeLock();

    public AggroList(NpcInstance npcInstance) {
        this.CF = npcInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDamageHate(Creature creature, int n, int n2) {
        if ((n = Math.max(n, 0)) == 0 && n2 == 0) {
            return;
        }
        this.writeLock.lock();
        try {
            AggroInfo aggroInfo = this.CG.get(creature.getObjectId());
            if (aggroInfo == null) {
                aggroInfo = new AggroInfo(creature);
                this.CG.put(creature.getObjectId(), aggroInfo);
            }
            aggroInfo.damage += n;
            aggroInfo.hate += n2;
            aggroInfo.damage = Math.max(aggroInfo.damage, 0);
            aggroInfo.hate = Math.max(aggroInfo.hate, 0);
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public AggroInfo get(Creature creature) {
        this.readLock.lock();
        try {
            AggroInfo aggroInfo = this.CG.get(creature.getObjectId());
            return aggroInfo;
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(Creature creature, boolean bl) {
        this.writeLock.lock();
        try {
            if (!bl) {
                this.CG.remove(creature.getObjectId());
                return;
            }
            AggroInfo aggroInfo = this.CG.get(creature.getObjectId());
            if (aggroInfo != null) {
                aggroInfo.hate = 0;
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void clear() {
        this.clear(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear(boolean bl) {
        this.writeLock.lock();
        try {
            if (this.CG.isEmpty()) {
                return;
            }
            if (!bl) {
                this.CG.clear();
                return;
            }
            TIntObjectIterator<AggroInfo> tIntObjectIterator = this.CG.iterator();
            while (tIntObjectIterator.hasNext()) {
                tIntObjectIterator.advance();
                AggroInfo aggroInfo = tIntObjectIterator.value();
                aggroInfo.hate = 0;
                if (aggroInfo.damage != 0) continue;
                tIntObjectIterator.remove();
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public boolean isEmpty() {
        this.readLock.lock();
        try {
            boolean bl = this.CG.isEmpty();
            return bl;
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Creature> getHateList(int n) {
        AggroInfo[] aggroInfoArray;
        this.readLock.lock();
        try {
            if (this.CG.isEmpty()) {
                List<Creature> list = Collections.emptyList();
                return list;
            }
            aggroInfoArray = this.CG.getValues(new AggroInfo[this.CG.size()]);
        }
        finally {
            this.readLock.unlock();
        }
        Arrays.sort(aggroInfoArray, HateComparator.getInstance());
        if (aggroInfoArray[0].hate == 0) {
            return Collections.emptyList();
        }
        LazyArrayList<Creature> lazyArrayList = new LazyArrayList<Creature>();
        List<Creature> list = World.getAroundCharacters(this.CF, n, n);
        block4: for (int i = 0; i < aggroInfoArray.length; ++i) {
            AggroInfo aggroInfo = aggroInfoArray[i];
            if (aggroInfo.hate == 0) continue;
            for (Creature creature : list) {
                if (creature.getObjectId() != aggroInfo.attackerId) continue;
                lazyArrayList.add(creature);
                continue block4;
            }
        }
        return lazyArrayList;
    }

    public Creature getMostHated() {
        AggroInfo[] aggroInfoArray;
        this.readLock.lock();
        try {
            if (this.CG.isEmpty()) {
                Creature creature = null;
                return creature;
            }
            aggroInfoArray = this.CG.getValues(new AggroInfo[this.CG.size()]);
        }
        finally {
            this.readLock.unlock();
        }
        Arrays.sort(aggroInfoArray, HateComparator.getInstance());
        if (aggroInfoArray[0].hate == 0) {
            return null;
        }
        List<Creature> list = World.getAroundCharacters(this.CF);
        block4: for (int i = 0; i < aggroInfoArray.length; ++i) {
            AggroInfo aggroInfo = aggroInfoArray[i];
            if (aggroInfo.hate == 0) continue;
            for (Creature creature : list) {
                if (creature.getObjectId() != aggroInfo.attackerId) continue;
                if (creature.isDead()) continue block4;
                return creature;
            }
        }
        return null;
    }

    public Creature getRandomHated() {
        AggroInfo[] aggroInfoArray;
        this.readLock.lock();
        try {
            if (this.CG.isEmpty()) {
                Creature creature = null;
                return creature;
            }
            aggroInfoArray = this.CG.getValues(new AggroInfo[this.CG.size()]);
        }
        finally {
            this.readLock.unlock();
        }
        Arrays.sort(aggroInfoArray, HateComparator.getInstance());
        if (aggroInfoArray[0].hate == 0) {
            return null;
        }
        List<Creature> list = World.getAroundCharacters(this.CF);
        LazyArrayList<Creature> lazyArrayList = LazyArrayList.newInstance();
        block4: for (int i = 0; i < aggroInfoArray.length; ++i) {
            AggroInfo aggroInfo = aggroInfoArray[i];
            if (aggroInfo.hate == 0) continue;
            for (Creature creature : list) {
                if (creature.getObjectId() != aggroInfo.attackerId) continue;
                if (creature.isDead()) continue block4;
                lazyArrayList.add(creature);
                continue block4;
            }
        }
        Creature creature = lazyArrayList.isEmpty() ? null : (Creature)lazyArrayList.get(Rnd.get(lazyArrayList.size()));
        LazyArrayList.recycle(lazyArrayList);
        return creature;
    }

    public Creature getTopDamager() {
        AggroInfo[] aggroInfoArray;
        this.readLock.lock();
        try {
            if (this.CG.isEmpty()) {
                Creature creature = null;
                return creature;
            }
            aggroInfoArray = this.CG.getValues(new AggroInfo[this.CG.size()]);
        }
        finally {
            this.readLock.unlock();
        }
        Creature creature = null;
        Arrays.sort(aggroInfoArray, DamageComparator.getInstance());
        if (aggroInfoArray[0].damage == 0) {
            return null;
        }
        List<Creature> list = World.getAroundCharacters(this.CF);
        for (int i = 0; i < aggroInfoArray.length; ++i) {
            AggroInfo aggroInfo = aggroInfoArray[i];
            if (aggroInfo.damage == 0) continue;
            for (Creature creature2 : list) {
                if (creature2.getObjectId() != aggroInfo.attackerId) continue;
                creature = creature2;
                return creature;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<Creature, HateInfo> getCharMap() {
        if (this.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<Creature, HateInfo> hashMap = new HashMap<Creature, HateInfo>();
        List<Creature> list = World.getAroundCharacters(this.CF);
        this.readLock.lock();
        try {
            TIntObjectIterator<AggroInfo> tIntObjectIterator = this.CG.iterator();
            block3: while (tIntObjectIterator.hasNext()) {
                tIntObjectIterator.advance();
                AggroInfo aggroInfo = tIntObjectIterator.value();
                if (aggroInfo.damage == 0 && aggroInfo.hate == 0) continue;
                for (Creature creature : list) {
                    if (creature.getObjectId() != aggroInfo.attackerId) continue;
                    hashMap.put(creature, new HateInfo(creature, aggroInfo));
                    continue block3;
                }
            }
        }
        finally {
            this.readLock.unlock();
        }
        return hashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<Playable, HateInfo> getPlayableMap() {
        if (this.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<Playable, HateInfo> hashMap = new HashMap<Playable, HateInfo>();
        List<Playable> list = World.getAroundPlayables(this.CF);
        this.readLock.lock();
        try {
            TIntObjectIterator<AggroInfo> tIntObjectIterator = this.CG.iterator();
            block3: while (tIntObjectIterator.hasNext()) {
                tIntObjectIterator.advance();
                AggroInfo aggroInfo = tIntObjectIterator.value();
                if (aggroInfo.damage == 0 && aggroInfo.hate == 0) continue;
                for (Playable playable : list) {
                    if (playable.getObjectId() != aggroInfo.attackerId) continue;
                    hashMap.put(playable, new HateInfo(playable, aggroInfo));
                    continue block3;
                }
            }
        }
        finally {
            this.readLock.unlock();
        }
        return hashMap;
    }

    public static class HateComparator
    implements Comparator<DamageHate> {
        private static Comparator<DamageHate> tS = new HateComparator();

        public static Comparator<DamageHate> getInstance() {
            return tS;
        }

        HateComparator() {
        }

        @Override
        public int compare(DamageHate damageHate, DamageHate damageHate2) {
            int n = damageHate2.hate - damageHate.hate;
            return n == 0 ? damageHate2.damage - damageHate.damage : n;
        }
    }

    public static class DamageComparator
    implements Comparator<DamageHate> {
        private static Comparator<DamageHate> tS = new DamageComparator();

        public static Comparator<DamageHate> getInstance() {
            return tS;
        }

        DamageComparator() {
        }

        @Override
        public int compare(DamageHate damageHate, DamageHate damageHate2) {
            return damageHate2.damage - damageHate.damage;
        }
    }

    public class AggroInfo
    extends DamageHate {
        public final int attackerId;

        AggroInfo(Creature creature) {
            this.attackerId = creature.getObjectId();
        }
    }

    public class HateInfo
    extends DamageHate {
        public final Creature attacker;

        HateInfo(Creature creature, AggroInfo aggroInfo) {
            this.attacker = creature;
            this.hate = aggroInfo.hate;
            this.damage = aggroInfo.damage;
        }
    }

    private abstract class DamageHate {
        public int hate;
        public int damage;

        private DamageHate() {
        }
    }
}

