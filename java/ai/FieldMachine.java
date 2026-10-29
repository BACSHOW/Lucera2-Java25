/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.List;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class FieldMachine
extends DefaultAI {
    private long bK;

    public FieldMachine(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature == null || creature.getPlayer() == null) {
            return;
        }
        if (System.currentTimeMillis() - this.bK > 15000L) {
            this.bK = System.currentTimeMillis();
            Functions.npcSayCustomMessage(npcInstance, "scripts.ai.FieldMachine." + npcInstance.getNpcId(), new Object[0]);
            List<NpcInstance> list = npcInstance.getAroundNpc(1500, 300);
            if (list != null && !list.isEmpty()) {
                for (NpcInstance npcInstance2 : list) {
                    if (!npcInstance2.isMonster() || npcInstance2.getNpcId() < 22656 || npcInstance2.getNpcId() > 22659) continue;
                    npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, 5000);
                }
            }
        }
    }
}

