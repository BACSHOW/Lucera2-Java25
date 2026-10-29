/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class KanadisGuide
extends Fighter {
    public KanadisGuide(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        NpcInstance npcInstance = this.getActor();
        List<NpcInstance> list = npcInstance.getAroundNpc(5000, 300);
        if (list != null && !list.isEmpty()) {
            for (NpcInstance npcInstance2 : list) {
                if (npcInstance2.getNpcId() != 36562) continue;
                npcInstance.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, npcInstance2, 5000);
            }
        }
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature.getNpcId() == 36562) {
            npcInstance.getAggroList().addDamageHate(creature, 0, 1);
            this.startRunningTask(2000L);
            this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected boolean maybeMoveToHome() {
        return false;
    }
}

