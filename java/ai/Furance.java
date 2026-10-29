/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Furance
extends DefaultAI {
    public Furance(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.startImmobilized();
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        NpcInstance npcInstance = this.getActor();
        if (Rnd.chance(50)) {
            npcInstance.setNpcState(1);
        }
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Switch(), 300000L, 300000L);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    public class Switch
    extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance npcInstance = Furance.this.getActor();
            if (npcInstance.getNpcState() == 1) {
                npcInstance.setNpcState(2);
            } else {
                npcInstance.setNpcState(1);
            }
        }
    }
}

