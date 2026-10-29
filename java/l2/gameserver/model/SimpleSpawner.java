/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.ArrayList;
import l2.commons.collections.MultiValueSet;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.Territory;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SimpleSpawner
extends Spawner
implements Cloneable {
    private static final Logger _log = LoggerFactory.getLogger(SimpleSpawner.class);
    private NpcTemplate Lh;
    private int Li;
    private int Lj;
    private int Lk;
    private int Ef;
    private Territory Au;

    public SimpleSpawner(NpcTemplate npcTemplate) {
        if (npcTemplate == null) {
            throw new NullPointerException();
        }
        this.Lh = npcTemplate;
        this._spawned = new ArrayList(1);
    }

    public SimpleSpawner(int n) {
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
        if (npcTemplate == null) {
            throw new NullPointerException("Not find npc: " + n);
        }
        this.Lh = npcTemplate;
        this._spawned = new ArrayList(1);
    }

    public int getAmount() {
        return this._maximumCount;
    }

    public int getSpawnedCount() {
        return this._currentCount.get();
    }

    public int getSheduledCount() {
        return this._scheduledCount.get();
    }

    public Territory getTerritory() {
        return this.Au;
    }

    public Location getLoc() {
        return new Location(this.Li, this.Lj, this.Lk);
    }

    public int getLocx() {
        return this.Li;
    }

    public int getLocy() {
        return this.Lj;
    }

    public int getLocz() {
        return this.Lk;
    }

    @Override
    public int getCurrentNpcId() {
        return this.Lh.getNpcId();
    }

    @Override
    public SpawnRange getCurrentSpawnRange() {
        if (this.Li == 0 && this.Lk == 0) {
            return this.Au;
        }
        return this.getLoc();
    }

    public int getHeading() {
        return this.Ef;
    }

    public void restoreAmount() {
        this._maximumCount = this._referenceCount;
    }

    public void setTerritory(Territory territory) {
        this.Au = territory;
    }

    public void setLoc(Location location) {
        this.Li = location.x;
        this.Lj = location.y;
        this.Lk = location.z;
        this.Ef = location.h;
    }

    public void setLocx(int n) {
        this.Li = n;
    }

    public void setLocy(int n) {
        this.Lj = n;
    }

    public void setLocz(int n) {
        this.Lk = n;
    }

    public void setHeading(int n) {
        this.Ef = n;
    }

    @Override
    public void decreaseCount(NpcInstance npcInstance) {
        this.decreaseCount0(this.Lh, npcInstance, npcInstance.getDeadTime());
    }

    @Override
    public NpcInstance doSpawn(boolean bl) {
        return this.doSpawn0(this.Lh, bl, StatsSet.EMPTY);
    }

    @Override
    protected NpcInstance initNpc(NpcInstance npcInstance, boolean bl, MultiValueSet<String> multiValueSet) {
        Location location;
        if (this.Au != null) {
            location = this.Au.getRandomLoc(this._reflection.getGeoIndex());
            location.setH(Rnd.get(65535));
        } else {
            location = this.getLoc();
            location.h = this.getHeading() == -1 ? Rnd.get(65535) : this.getHeading();
        }
        return this.initNpc0(npcInstance, location, bl, multiValueSet);
    }

    @Override
    public void respawnNpc(NpcInstance npcInstance) {
        npcInstance.refreshID();
        this.initNpc(npcInstance, true, StatsSet.EMPTY);
    }

    public SimpleSpawner clone() {
        SimpleSpawner simpleSpawner = new SimpleSpawner(this.Lh);
        simpleSpawner.setTerritory(this.Au);
        simpleSpawner.setLocx(this.Li);
        simpleSpawner.setLocy(this.Lj);
        simpleSpawner.setLocz(this.Lk);
        simpleSpawner.setHeading(this.Ef);
        simpleSpawner.setAmount(this._maximumCount);
        simpleSpawner.setRespawnDelay(this._respawnDelay, this._respawnDelayRandom);
        simpleSpawner.setRespawnCron(this.getRespawnCron());
        return simpleSpawner;
    }
}

