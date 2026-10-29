/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.instancemanager.sepulchers.spawn.ISepulcherSpawnProperty;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnDefine;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class SepulcherMonsterInstance
extends MonsterInstance
implements ISepulcherSpawnProperty {
    protected SepulcherSpawnDefine define;

    public SepulcherMonsterInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onDespawn() {
        super.onDespawn();
        this.define.notifyDespawn(this);
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

