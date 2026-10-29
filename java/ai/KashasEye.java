/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class KashasEye
extends DefaultAI {
    public KashasEye(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }

    @Override
    protected void onEvtDead(Creature creature) {
        super.onEvtDead(creature);
        NpcInstance npcInstance = this.getActor();
        if (npcInstance != null && creature != null && npcInstance != creature && Rnd.chance(35)) {
            npcInstance.setDisplayId(Rnd.get(18812, 18814));
        }
    }
}

