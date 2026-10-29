/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class HekatonPrime
extends Fighter {
    private long cj;

    public HekatonPrime(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        this.cj = System.currentTimeMillis();
    }

    @Override
    protected boolean thinkActive() {
        if (this.cj + 600000L < System.currentTimeMillis()) {
            if (this.getActor().getMinionList().hasMinions()) {
                this.getActor().getMinionList().deleteMinions();
            }
            this.getActor().deleteMe();
            return true;
        }
        return false;
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        this.cj = System.currentTimeMillis();
        super.onEvtAttacked(creature, n);
    }
}

