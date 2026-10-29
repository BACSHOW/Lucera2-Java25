/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Kama56Minion
extends Fighter {
    public Kama56Minion(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.setIsInvul(true);
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
        if (n < 10000000) {
            return;
        }
        super.onEvtAggression(creature, n);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }
}

