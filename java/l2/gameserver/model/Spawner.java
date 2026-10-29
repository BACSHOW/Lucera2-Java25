/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.collections.MultiValueSet;
import l2.commons.time.cron.NextTime;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.EventOwner;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.taskmanager.SpawnTaskManager;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Spawner
extends EventOwner
implements Cloneable {
    private static final long serialVersionUID = 1L;
    protected static final Logger _log = LoggerFactory.getLogger(Spawner.class);
    protected static final int MIN_RESPAWN_DELAY = 300;
    protected int _maximumCount;
    protected int _referenceCount;
    protected AtomicInteger _currentCount = new AtomicInteger(0);
    protected AtomicInteger _scheduledCount = new AtomicInteger(0);
    protected long _respawnDelay;
    protected long _respawnDelayRandom;
    protected NextTime _respawnCron;
    protected int _respawnTime;
    protected boolean _doRespawn;
    protected NpcInstance _lastSpawn;
    protected List<NpcInstance> _spawned;
    protected Reflection _reflection = ReflectionManager.DEFAULT;

    public void decreaseScheduledCount() {
        int n;
        while ((n = this._scheduledCount.get()) > 0) {
            if (!this._scheduledCount.compareAndSet(n, n - 1)) continue;
            return;
        }
    }

    public boolean isDoRespawn() {
        return this._doRespawn;
    }

    public Reflection getReflection() {
        return this._reflection;
    }

    public void setReflection(Reflection reflection) {
        this._reflection = reflection;
    }

    public long getRespawnDelay() {
        return this._respawnDelay;
    }

    public long getRespawnDelayRandom() {
        return this._respawnDelayRandom;
    }

    public long getRespawnDelayWithRnd() {
        if (this._respawnDelayRandom == 0L) {
            return this._respawnDelay;
        }
        return Rnd.get(this._respawnDelay - this._respawnDelayRandom, this._respawnDelay);
    }

    public int getRespawnTime() {
        return this._respawnTime;
    }

    public NpcInstance getLastSpawn() {
        return this._lastSpawn;
    }

    public void setAmount(int n) {
        if (this._referenceCount == 0) {
            this._referenceCount = n;
        }
        this._maximumCount = n;
    }

    public void deleteAll() {
        this.stopRespawn();
        for (NpcInstance npcInstance : this._spawned) {
            npcInstance.deleteMe();
        }
        this._spawned.clear();
        this._respawnTime = 0;
        this._scheduledCount.set(0);
        this._currentCount.set(0);
    }

    public abstract void decreaseCount(NpcInstance var1);

    public abstract NpcInstance doSpawn(boolean var1);

    public abstract void respawnNpc(NpcInstance var1);

    protected abstract NpcInstance initNpc(NpcInstance var1, boolean var2, MultiValueSet<String> var3);

    public abstract int getCurrentNpcId();

    public abstract SpawnRange getCurrentSpawnRange();

    public int init() {
        while (this._currentCount.get() + this._scheduledCount.get() < this._maximumCount) {
            this.doSpawn(false);
        }
        this._doRespawn = true;
        return this._currentCount.get();
    }

    public NpcInstance spawnOne() {
        return this.doSpawn(false);
    }

    public void stopRespawn() {
        this._doRespawn = false;
    }

    public void startRespawn() {
        this._doRespawn = true;
    }

    public List<NpcInstance> getAllSpawned() {
        return this._spawned;
    }

    public NpcInstance getFirstSpawned() {
        List<NpcInstance> list = this.getAllSpawned();
        return list.size() > 0 ? list.get(0) : null;
    }

    public void setRespawnDelay(long l, long l2) {
        if (l < 0L) {
            _log.warn("respawn delay is negative");
        }
        this._respawnDelay = l;
        this._respawnDelayRandom = l2;
    }

    public void setRespawnDelay(int n) {
        this.setRespawnDelay(n, 0L);
    }

    public void setRespawnTime(int n) {
        this._respawnTime = n;
    }

    public NextTime getRespawnCron() {
        return this._respawnCron;
    }

    public void setRespawnCron(NextTime nextTime) {
        this._respawnCron = nextTime;
    }

    protected NpcInstance doSpawn0(NpcTemplate npcTemplate, boolean bl, MultiValueSet<String> multiValueSet) {
        if (npcTemplate.isInstanceOf(PetInstance.class) || npcTemplate.isInstanceOf(MinionInstance.class)) {
            this._currentCount.incrementAndGet();
            return null;
        }
        NpcInstance npcInstance = npcTemplate.getNewInstance();
        if (npcInstance == null) {
            return null;
        }
        if (!bl) {
            bl = (long)this._respawnTime <= System.currentTimeMillis() / 1000L + 300L;
        }
        return this.initNpc(npcInstance, bl, multiValueSet);
    }

    protected NpcInstance initNpc0(NpcInstance npcInstance, Location location, boolean bl, MultiValueSet<String> multiValueSet) {
        npcInstance.setParameters(multiValueSet);
        npcInstance.setCurrentHpMp(npcInstance.getMaxHp(), npcInstance.getMaxMp(), true);
        npcInstance.setSpawn(this);
        npcInstance.setSpawnedLoc(location);
        npcInstance.setUnderground(GeoEngine.getHeight(location, this.getReflection().getGeoIndex()) < GeoEngine.getHeight(location.clone().changeZ(5000), this.getReflection().getGeoIndex()));
        for (GlobalEvent globalEvent : this.getEvents()) {
            npcInstance.addEvent(globalEvent);
        }
        if (bl) {
            npcInstance.setReflection(this.getReflection());
            if (npcInstance.isMonster()) {
                ((MonsterInstance)npcInstance).setChampion();
            }
            npcInstance.spawnMe(location);
            this._currentCount.incrementAndGet();
        } else {
            npcInstance.setLoc(location);
            this._scheduledCount.incrementAndGet();
            SpawnTaskManager.getInstance().addSpawnTask(npcInstance, (long)this._respawnTime * 1000L - System.currentTimeMillis());
        }
        this._spawned.add(npcInstance);
        this._lastSpawn = npcInstance;
        return npcInstance;
    }

    public void decreaseCount0(NpcTemplate npcTemplate, NpcInstance npcInstance, long l) {
        int n;
        while ((n = this._currentCount.get()) > 0 && !this._currentCount.compareAndSet(n, n - 1)) {
        }
        if (this.getRespawnDelay() == 0L && this.getRespawnCron() == null) {
            return;
        }
        if (this._doRespawn && this._scheduledCount.get() + this._currentCount.get() < this._maximumCount) {
            long l2 = System.currentTimeMillis();
            long l3 = this.getRespawnCron() == null ? (npcTemplate.isRaid ? (long)(Config.ALT_RAID_RESPAWN_MULTIPLIER * (double)this.getRespawnDelayWithRnd()) * 1000L : this.getRespawnDelayWithRnd() * 1000L) : this.getRespawnCron().next(l2) - l2;
            l3 = Math.max(1000L, l3 - l);
            this._respawnTime = (int)((l2 + l3) / 1000L);
            this._scheduledCount.incrementAndGet();
            SpawnTaskManager.getInstance().addSpawnTask(npcInstance, l3);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (NpcInstance npcInstance : this._spawned) {
            stringBuilder.append(npcInstance.getNpcId());
        }
        return "Spawner{_currentCount=" + this._currentCount + ", _maximumCount=" + this._maximumCount + ", _referenceCount=" + this._referenceCount + ", _scheduledCount=" + this._scheduledCount + ", _respawnDelay=" + this._respawnDelay + ", _respawnCron=" + this._respawnCron + ", _respawnDelayRandom=" + this._respawnDelayRandom + ", _respawnTime=" + this._respawnTime + ", _doRespawn=" + this._doRespawn + ", _lastSpawn=" + this._lastSpawn + ", _spawned=" + this._spawned + ", _reflection=" + this._reflection + '}';
    }
}

