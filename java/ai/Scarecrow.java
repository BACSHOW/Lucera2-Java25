/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Scarecrow
extends Fighter {
    public Scarecrow(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.block();
        npcInstance.setIsInvul(true);
    }

    @Override
    protected void onIntentionAttack(Creature creature) {
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}

