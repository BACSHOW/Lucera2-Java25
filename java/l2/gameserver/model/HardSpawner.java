/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.spawn.SpawnNpcInfo;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.templates.spawn.SpawnTemplate;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

public class HardSpawner
extends Spawner {
    private IntObjectMap<Queue<NpcInstance>> FN = new CHashIntObjectMap<Queue<NpcInstance>>();
    private final SpawnTemplate FO;
    private int FP;
    private int FQ;
    private List<NpcInstance> FR = new CopyOnWriteArrayList<NpcInstance>();

    public HardSpawner(SpawnTemplate spawnTemplate) {
        this.FO = spawnTemplate;
        this._spawned = new CopyOnWriteArrayList();
    }

    @Override
    public void decreaseCount(NpcInstance npcInstance) {
        this.e(npcInstance);
        this._spawned.remove(npcInstance);
        SpawnNpcInfo spawnNpcInfo = this.cj();
        NpcInstance npcInstance2 = this.y(spawnNpcInfo.getTemplate().getNpcId());
        if (npcInstance2 == null) {
            npcInstance2 = spawnNpcInfo.getTemplate().getNewInstance();
        } else {
            npcInstance2.refreshID();
        }
        npcInstance2.setSpawn(this);
        this.FR.add(npcInstance2);
        this.decreaseCount0(spawnNpcInfo.getTemplate(), npcInstance2, npcInstance.getDeadTime());
    }

    @Override
    public NpcInstance doSpawn(boolean bl) {
        SpawnNpcInfo spawnNpcInfo = this.cj();
        return this.doSpawn0(spawnNpcInfo.getTemplate(), bl, spawnNpcInfo.getParameters());
    }

    @Override
    protected NpcInstance initNpc(NpcInstance npcInstance, boolean bl, MultiValueSet<String> multiValueSet) {
        this.FR.remove(npcInstance);
        SpawnRange spawnRange = this.FO.getSpawnRange(this.ck());
        npcInstance.setSpawnRange(spawnRange);
        return this.initNpc0(npcInstance, spawnRange.getRandomLoc(this.getReflection().getGeoIndex()), bl, multiValueSet);
    }

    @Override
    public int getCurrentNpcId() {
        SpawnNpcInfo spawnNpcInfo = this.FO.getNpcId(this.FQ);
        return spawnNpcInfo.getTemplate().npcId;
    }

    @Override
    public SpawnRange getCurrentSpawnRange() {
        return this.FO.getSpawnRange(this.FP);
    }

    @Override
    public void respawnNpc(NpcInstance npcInstance) {
        this.initNpc(npcInstance, true, StatsSet.EMPTY);
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        for (NpcInstance object : this.FR) {
            this.e(object);
        }
        this.FR.clear();
        for (Collection collection : this.FN.values()) {
            collection.clear();
        }
        this.FN.clear();
    }

    private synchronized SpawnNpcInfo cj() {
        SpawnNpcInfo spawnNpcInfo = null;
        int n = 0;
        int n2 = this.FQ++;
        if (this.FQ >= this.FO.getNpcSize()) {
            this.FQ = 0;
        }
        if ((spawnNpcInfo = this.FO.getNpcId(n2)).getMax() > 0) {
            int n3 = 0;
            for (NpcInstance npcInstance : this._spawned) {
                if (npcInstance.getNpcId() != spawnNpcInfo.getTemplate().getNpcId()) continue;
                ++n3;
            }
            if (n3 >= spawnNpcInfo.getMax() && n++ > this.FO.getNpcSize() * 2) {
                throw new IllegalStateException("getNextNpcInfo failed (" + n3 + ", " + spawnNpcInfo.getMax() + ", " + spawnNpcInfo.getNpcId() + ")");
            }
        }
        return spawnNpcInfo;
    }

    private synchronized int ck() {
        int n = this.FP++;
        if (this.FP >= this.FO.getSpawnRangeSize()) {
            this.FP = 0;
        }
        return n;
    }

    public HardSpawner clone() {
        HardSpawner hardSpawner = new HardSpawner(this.FO);
        hardSpawner.setAmount(this._maximumCount);
        hardSpawner.setRespawnDelay(this._respawnDelay, this._respawnDelayRandom);
        hardSpawner.setRespawnTime(0);
        hardSpawner.setRespawnCron(this.getRespawnCron());
        return hardSpawner;
    }

    private void e(NpcInstance npcInstance) {
        npcInstance.setSpawn(null);
        npcInstance.decayMe();
        Queue<NpcInstance> queue = this.FN.get(npcInstance.getNpcId());
        if (queue == null) {
            queue = new ArrayDeque<NpcInstance>();
            this.FN.put(npcInstance.getNpcId(), queue);
        }
        queue.add(npcInstance);
    }

    private NpcInstance y(int n) {
        Queue<NpcInstance> queue = this.FN.get(n);
        if (queue == null) {
            return null;
        }
        NpcInstance npcInstance = queue.poll();
        if (npcInstance != null && npcInstance.isDeleted()) {
            _log.info("Npc: " + n + " is deleted, cant used by cache.");
            return this.y(n);
        }
        return npcInstance;
    }

    public SpawnTemplate getTemplate() {
        return this.FO;
    }
}

