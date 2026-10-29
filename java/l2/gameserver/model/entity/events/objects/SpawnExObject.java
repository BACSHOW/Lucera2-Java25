/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.SpawnableObject;
import l2.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnExObject
implements SpawnableObject {
    private static final Logger _log = LoggerFactory.getLogger(SpawnExObject.class);
    private final List<Spawner> _spawns;
    private boolean cs;
    private String _name;

    public SpawnExObject(String string) {
        this._name = string;
        this._spawns = SpawnManager.getInstance().getSpawners(this._name);
        if (this._spawns.isEmpty() && Config.ALT_DEBUG_ENABLED) {
            _log.info("SpawnExObject: not found spawn group: " + string);
        }
    }

    @Override
    public void spawnObject(GlobalEvent globalEvent) {
        if (this.cs) {
            _log.info("SpawnExObject: can't spawn twice: " + this._name + "; event: " + globalEvent, new Exception());
        } else {
            for (Spawner spawner : this._spawns) {
                if (globalEvent.isInProgress()) {
                    spawner.addEvent(globalEvent);
                } else {
                    spawner.removeEvent(globalEvent);
                }
                spawner.setReflection(globalEvent.getReflection());
                spawner.init();
            }
            this.cs = true;
        }
    }

    @Override
    public void despawnObject(GlobalEvent globalEvent) {
        if (!this.cs) {
            return;
        }
        this.cs = false;
        for (Spawner spawner : this._spawns) {
            spawner.removeEvent(globalEvent);
            spawner.deleteAll();
        }
    }

    @Override
    public void refreshObject(GlobalEvent globalEvent) {
        for (NpcInstance npcInstance : this.getAllSpawned()) {
            if (globalEvent.isInProgress()) {
                npcInstance.addEvent(globalEvent);
                continue;
            }
            npcInstance.removeEvent(globalEvent);
        }
    }

    public List<Spawner> getSpawns() {
        return this._spawns;
    }

    public List<NpcInstance> getAllSpawned() {
        ArrayList arrayList = new ArrayList();
        for (Spawner spawner : this._spawns) {
            arrayList.addAll(spawner.getAllSpawned());
        }
        return arrayList.isEmpty() ? Collections.emptyList() : arrayList;
    }

    public NpcInstance getFirstSpawned() {
        List<NpcInstance> list = this.getAllSpawned();
        return list.size() > 0 ? list.get(0) : null;
    }

    public boolean isSpawned() {
        return this.cs;
    }
}

