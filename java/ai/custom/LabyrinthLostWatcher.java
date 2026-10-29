/*
 * Decompiled with CFR 0.152.
 */
package ai.custom;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.ReflectionBossInstance;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.FuncSet;

public class LabyrinthLostWatcher
extends Fighter {
    public LabyrinthLostWatcher(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        Reflection reflection = npcInstance.getReflection();
        if (!reflection.isDefault() && this.c(npcInstance.getNpcId()) && this.o() != null) {
            this.o().addStatFunc(new FuncSet(Stats.POWER_DEFENCE, 48, this, (double)this.o().getTemplate().basePDef * 0.66));
        }
        super.onEvtDead(creature);
    }

    private boolean c(int n) {
        for (NpcInstance npcInstance : this.getActor().getReflection().getNpcs()) {
            if (npcInstance.getNpcId() != n || npcInstance.isDead()) continue;
            return false;
        }
        return true;
    }

    private NpcInstance o() {
        for (NpcInstance npcInstance : this.getActor().getReflection().getNpcs()) {
            if (!(npcInstance instanceof ReflectionBossInstance)) continue;
            return npcInstance;
        }
        return null;
    }
}

