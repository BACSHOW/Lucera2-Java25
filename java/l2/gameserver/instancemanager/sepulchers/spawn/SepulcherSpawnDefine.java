/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers.spawn;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.sepulchers.FourSepulchers;
import l2.gameserver.instancemanager.sepulchers.spawn.ISepulcherSpawnProperty;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SepulcherSpawnDefine {
    protected static final Logger log = LoggerFactory.getLogger(SepulcherSpawnDefine.class);
    private final Location Cn;
    private final int Co;
    private final int count;
    private final SepulcherSpawnHandler Cp;
    private final NpcTemplate Cq;
    private final ConcurrentLinkedQueue<ScheduledFuture<?>> Cr = new ConcurrentLinkedQueue();
    private final ConcurrentLinkedQueue<NpcInstance> Cs = new ConcurrentLinkedQueue();

    public SepulcherSpawnDefine(int n, Location location, int n2, int n3, SepulcherSpawnHandler sepulcherSpawnHandler) {
        this.Cn = location;
        this.Co = n2;
        this.count = n3;
        this.Cp = sepulcherSpawnHandler;
        this.Cq = NpcHolder.getInstance().getTemplate(n);
    }

    public void spawn() {
        for (int i = 0; i < this.count; ++i) {
            this.bS();
        }
    }

    public void despawn() {
        this.Cr.forEach(scheduledFuture -> scheduledFuture.cancel(false));
        this.Cs.forEach(GameObject::deleteMe);
        this.Cr.clear();
        this.Cs.clear();
    }

    private Location bQ() {
        return this.Cn == null ? this.Cp.getTerritory().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex()) : this.Cn;
    }

    private NpcInstance bR() {
        NpcInstance npcInstance = this.Cq.getNewInstance();
        if (npcInstance == null) {
            log.warn("Failed to spawn " + this.Cq);
            return null;
        }
        if (!ISepulcherSpawnProperty.class.isAssignableFrom(npcInstance.getClass())) {
            log.warn("Incorrect npc sepulcher instance! " + npcInstance);
            return null;
        }
        npcInstance.setReflection(0);
        npcInstance.setCurrentHpMp(npcInstance.getMaxHp(), npcInstance.getMaxMp(), true);
        ((ISepulcherSpawnProperty)((Object)npcInstance)).setSepulcherSpawnDefine(this);
        return npcInstance;
    }

    private void bS() {
        Location location = this.bQ();
        if (location == null) {
            log.warn("Null spawn pos " + this);
            return;
        }
        NpcInstance npcInstance = this.bR();
        if (npcInstance == null) {
            return;
        }
        npcInstance.setSpawnedLoc(location);
        npcInstance.spawnMe(location);
        FourSepulchers.getInstance().log("Spawning " + npcInstance);
        this.Cs.add(npcInstance);
        this.Cp.onSpawn(npcInstance);
    }

    private void bT() {
        this.Cr.add(ThreadPoolManager.getInstance().schedule(() -> {
            if (this.Cp.isActive()) {
                this.bS();
            }
            this.Cr.removeIf(Future::isDone);
        }, this.Co * 1000));
    }

    public void notifyDespawn(NpcInstance npcInstance) {
        this.Cs.remove(npcInstance);
        this.Cp.onDespawn(npcInstance);
        if (this.Co > 0 && this.Cp.isActive()) {
            this.bT();
        }
    }

    public ConcurrentLinkedQueue<NpcInstance> getAlive() {
        return this.Cs;
    }

    public SepulcherSpawnHandler getHandler() {
        return this.Cp;
    }
}

