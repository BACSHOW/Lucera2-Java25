/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class FrightenedOrc
extends Fighter {
    private boolean ca;

    public FrightenedOrc(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtSpawn() {
        this.ca = true;
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature != null && Rnd.chance(10) && this.ca) {
            Functions.npcSay(npcInstance, "Don't kill me! If you show mercy I will pay you 10000 adena!");
            this.ca = false;
        }
        super.onEvtAttacked(creature, n);
    }
}

