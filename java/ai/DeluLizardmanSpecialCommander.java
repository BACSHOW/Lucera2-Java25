/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class DeluLizardmanSpecialCommander
extends Fighter {
    private boolean bA = false;

    public DeluLizardmanSpecialCommander(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtSpawn() {
        this.bA = false;
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (Rnd.chance(40) && !this.bA) {
            this.bA = true;
            Functions.npcSay(npcInstance, "Come on my fellows, assist me here!");
            List<NpcInstance> list = npcInstance.getAroundNpc(1000, 300);
            if (list != null && !list.isEmpty()) {
                for (NpcInstance npcInstance2 : list) {
                    if (!npcInstance2.isMonster()) continue;
                    npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, 5000);
                }
            }
        }
        super.onEvtAttacked(creature, n);
    }
}

