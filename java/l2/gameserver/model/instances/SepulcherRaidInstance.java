/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.instancemanager.sepulchers.spawn.ISepulcherSpawnProperty;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnDefine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SepulcherRaidInstance
extends RaidBossInstance
implements ISepulcherSpawnProperty {
    private static final Logger sQ = LoggerFactory.getLogger(SepulcherRaidInstance.class);
    private static final int VR = 31452;
    protected SepulcherSpawnDefine define;

    public SepulcherRaidInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onDespawn() {
        super.onDespawn();
        this.define.notifyDespawn(this);
    }

    @Override
    protected void onDeath(Creature creature) {
        try {
            super.onDeath(creature);
            this.define.getHandler().spawnCustomSingle(31452, this.getLoc());
        }
        catch (Exception exception) {
            sQ.error(exception.getMessage(), exception);
        }
    }

    @Override
    public SepulcherSpawnDefine getSepulcherSpawnDefine() {
        return this.define;
    }

    @Override
    public void setSepulcherSpawnDefine(SepulcherSpawnDefine sepulcherSpawnDefine) {
        this.define = sepulcherSpawnDefine;
    }
}

