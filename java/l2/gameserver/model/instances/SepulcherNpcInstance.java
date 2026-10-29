/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.instancemanager.sepulchers.spawn.ISepulcherSpawnProperty;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnDefine;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class SepulcherNpcInstance
extends NpcInstance
implements ISepulcherSpawnProperty {
    protected SepulcherSpawnDefine spawnDefine;

    public SepulcherNpcInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public SepulcherSpawnDefine getSepulcherSpawnDefine() {
        return this.spawnDefine;
    }

    @Override
    public void setSepulcherSpawnDefine(SepulcherSpawnDefine sepulcherSpawnDefine) {
        this.spawnDefine = sepulcherSpawnDefine;
    }

    @Override
    protected void onDespawn() {
        super.onDespawn();
        this.spawnDefine.notifyDespawn(this);
    }
}

