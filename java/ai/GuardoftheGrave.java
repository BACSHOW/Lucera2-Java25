/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.Fighter;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class GuardoftheGrave
extends Fighter {
    private static final int ch = 90000;
    private static final int ci = 18816;

    public GuardoftheGrave(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.setIsInvul(true);
        npcInstance.startImmobilized();
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.getInstance().schedule(new DeSpawnTask(), 90000 + Rnd.get(1, 30));
    }

    @Override
    protected boolean checkTarget(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance != null && creature != null && !npcInstance.isInRange(creature, (long)npcInstance.getAggroRange())) {
            npcInstance.getAggroList().remove(creature, true);
            return false;
        }
        return super.checkTarget(creature, n);
    }

    protected void spawnChest(NpcInstance npcInstance) {
        try {
            NpcInstance npcInstance2 = NpcHolder.getInstance().getTemplate(18816).getNewInstance();
            npcInstance2.setSpawnedLoc(npcInstance.getLoc());
            npcInstance2.setCurrentHpMp(npcInstance2.getMaxHp(), npcInstance2.getMaxMp(), true);
            npcInstance2.spawnMe(npcInstance2.getSpawnedLoc());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private class DeSpawnTask
    extends RunnableImpl {
        private DeSpawnTask() {
        }

        @Override
        public void runImpl() {
            NpcInstance npcInstance = GuardoftheGrave.this.getActor();
            GuardoftheGrave.this.spawnChest(npcInstance);
            npcInstance.deleteMe();
        }
    }
}

