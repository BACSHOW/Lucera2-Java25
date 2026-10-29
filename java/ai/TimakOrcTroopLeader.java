/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class TimakOrcTroopLeader
extends Fighter {
    private static final int[] da = new int[]{20768, 20769, 20770};
    private boolean aZ = true;

    public TimakOrcTroopLeader(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (!npcInstance.isDead() && this.aZ) {
            this.aZ = false;
            Functions.npcSay(npcInstance, "Show yourselves!");
            for (int n2 : da) {
                try {
                    NpcInstance npcInstance2 = NpcHolder.getInstance().getTemplate(n2).getNewInstance();
                    npcInstance2.setSpawnedLoc(((MonsterInstance)npcInstance).getMinionPosition());
                    npcInstance2.setReflection(npcInstance.getReflection());
                    npcInstance2.setCurrentHpMp(npcInstance2.getMaxHp(), npcInstance2.getMaxMp(), true);
                    npcInstance2.spawnMe(npcInstance2.getSpawnedLoc());
                    npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(1, 100));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.aZ = true;
        super.onEvtDead(creature);
    }
}

