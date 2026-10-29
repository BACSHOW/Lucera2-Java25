/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class Tiberias
extends Fighter {
    public Tiberias(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        Functions.npcShoutCustomMessage(npcInstance, "scripts.ai.Tiberias.kill", new Object[0]);
        super.onEvtDead(creature);
    }
}

