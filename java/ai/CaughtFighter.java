/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class CaughtFighter
extends Fighter {
    private static final int bv = 60000;
    private final long bw = System.currentTimeMillis() + 60000L;

    public CaughtFighter(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        if (Rnd.chance(75)) {
            Functions.npcSayCustomMessage(this.getActor(), "scripts.ai.CaughtMob.spawn", new Object[0]);
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        if (Rnd.chance(75)) {
            Functions.npcSayCustomMessage(this.getActor(), "scripts.ai.CaughtMob.death", new Object[0]);
        }
        super.onEvtDead(creature);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance != null && System.currentTimeMillis() >= this.bw) {
            npcInstance.deleteMe();
            return false;
        }
        return super.thinkActive();
    }
}

