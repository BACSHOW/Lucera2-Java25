/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.spawn;

import java.util.ArrayList;
import java.util.List;
import l2.commons.time.cron.NextTime;
import l2.gameserver.templates.spawn.PeriodOfDay;
import l2.gameserver.templates.spawn.SpawnNpcInfo;
import l2.gameserver.templates.spawn.SpawnRange;

public class SpawnTemplate {
    private final String awk;
    private final String awl;
    private final PeriodOfDay awm;
    private final int _count;
    private final long awn;
    private final long awo;
    private final NextTime _respawnCron;
    private final List<SpawnNpcInfo> awp = new ArrayList<SpawnNpcInfo>(1);
    private final List<SpawnRange> awq = new ArrayList<SpawnRange>(1);

    public SpawnTemplate(String string, String string2, PeriodOfDay periodOfDay, int n, long l, long l2, NextTime nextTime) {
        this.awk = string;
        this.awl = string2;
        this.awm = periodOfDay;
        this._count = n;
        this.awn = l;
        this.awo = l2;
        this._respawnCron = nextTime;
    }

    public void addSpawnRange(SpawnRange spawnRange) {
        this.awq.add(spawnRange);
    }

    public SpawnRange getSpawnRange(int n) {
        return this.awq.get(n);
    }

    public void addNpc(SpawnNpcInfo spawnNpcInfo) {
        this.awp.add(spawnNpcInfo);
    }

    public SpawnNpcInfo getNpcId(int n) {
        return this.awp.get(n);
    }

    public String getMakerName() {
        return this.awk;
    }

    public int getNpcSize() {
        return this.awp.size();
    }

    public int getSpawnRangeSize() {
        return this.awq.size();
    }

    public int getCount() {
        return this._count;
    }

    public long getRespawn() {
        return this.awn;
    }

    public long getRespawnRandom() {
        return this.awo;
    }

    public NextTime getRespawnCron() {
        return this._respawnCron;
    }

    public PeriodOfDay getPeriodOfDay() {
        return this.awm;
    }

    public String getEventName() {
        return this.awl;
    }
}

