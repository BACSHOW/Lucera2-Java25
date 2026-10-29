/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;

public class SealDevice
extends Fighter {
    private boolean cK = false;

    public SealDevice(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.block();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (!this.cK) {
            npcInstance.broadcastPacket(new MagicSkillUse(npcInstance, npcInstance, 5980, 1, 0, 0L));
            this.cK = true;
        }
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}

