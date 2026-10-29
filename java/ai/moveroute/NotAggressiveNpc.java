/*
 * Decompiled with CFR 0.152.
 */
package ai.moveroute;

import ai.moveroute.MoveRouteDefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class NotAggressiveNpc
extends MoveRouteDefaultAI {
    public NotAggressiveNpc(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}

