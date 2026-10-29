/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.scripts.Functions;

public class Core
extends Fighter {
    private boolean aZ = true;

    public Core(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (this.aZ) {
            Functions.npcSay(npcInstance, "A non-permitted target has been discovered.");
            Functions.npcSay(npcInstance, "Starting intruder removal system.");
            this.aZ = false;
        } else if (Rnd.chance(1)) {
            Functions.npcSay(npcInstance, "Removing intruders.");
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        npcInstance.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, npcInstance.getLoc()));
        Functions.npcSay(npcInstance, "A fatal error has occurred");
        Functions.npcSay(npcInstance, "System is being shut down...");
        Functions.npcSay(npcInstance, "......");
        this.aZ = true;
        super.onEvtDead(creature);
    }
}

