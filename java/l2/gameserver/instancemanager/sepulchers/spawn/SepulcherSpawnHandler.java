/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers.spawn;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import l2.gameserver.instancemanager.sepulchers.FourSepulchers;
import l2.gameserver.instancemanager.sepulchers.event.EventHandler;
import l2.gameserver.instancemanager.sepulchers.event.OnEveryoneDespawned;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnDefine;
import l2.gameserver.model.Territory;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public class SepulcherSpawnHandler {
    private final String name;
    private final Territory Ct;
    private final List<SepulcherSpawnDefine> Cu;
    private final ConcurrentLinkedQueue<SepulcherSpawnDefine> Cv = new ConcurrentLinkedQueue();
    private final EventHandler Cw = new EventHandler();
    private volatile boolean active = false;

    public SepulcherSpawnHandler(String string, Territory territory, List<SepulcherSpawnDefine> list) {
        this.name = string;
        this.Ct = territory;
        this.Cu = list;
    }

    public void spawn() {
        this.active = true;
        this.Cw.reset();
        this.Cu.forEach(SepulcherSpawnDefine::spawn);
    }

    public void despawn() {
        this.active = false;
        this.Cu.forEach(SepulcherSpawnDefine::despawn);
        this.Cv.forEach(SepulcherSpawnDefine::despawn);
        this.Cv.clear();
    }

    public int aliveCount() {
        return this.Cu.stream().mapToInt(sepulcherSpawnDefine -> sepulcherSpawnDefine.getAlive().size()).sum();
    }

    public Territory getTerritory() {
        return this.Ct;
    }

    public void onSpawn(NpcInstance npcInstance) {
        FourSepulchers.getInstance().log("Spawning " + npcInstance);
    }

    public void onDespawn(NpcInstance npcInstance) {
        int n = this.aliveCount();
        FourSepulchers.getInstance().log("despawn notice: " + npcInstance + ". Alive: " + n);
        if (n < 10) {
            FourSepulchers.getInstance().log("Alive monsters: " + this.Cu.stream().map(sepulcherSpawnDefine -> sepulcherSpawnDefine.hashCode() + ":[" + sepulcherSpawnDefine.getAlive().stream().map(NpcInstance::toString).collect(Collectors.joining(", ")) + "]").collect(Collectors.joining(", ")));
        }
        if (this.active && this.aliveCount() == 0) {
            this.Cw.trigger(OnEveryoneDespawned.class, OnEveryoneDespawned::onAction);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void spawnCustom(int n, Location location, int n2, int n3, boolean bl) {
        FourSepulchers.getInstance().log("spawn custom " + n + " " + location);
        SepulcherSpawnDefine sepulcherSpawnDefine = new SepulcherSpawnDefine(n, location, n2, n3, this);
        this.Cv.add(sepulcherSpawnDefine);
        if (bl) {
            sepulcherSpawnDefine.spawn();
        }
    }

    public void spawnCustomSingle(int n, Location location) {
        this.spawnCustom(n, location, 0, 1, true);
    }

    public List<NpcInstance> collectAlive() {
        return this.Cu.stream().flatMap(sepulcherSpawnDefine -> sepulcherSpawnDefine.getAlive().stream()).collect(Collectors.toList());
    }

    public EventHandler getEventHandler() {
        return this.Cw;
    }

    public String getName() {
        return this.name;
    }
}

