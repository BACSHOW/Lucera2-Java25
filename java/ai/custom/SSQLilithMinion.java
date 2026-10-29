/*
 * Decompiled with CFR 0.152.
 */
package ai.custom;

import java.util.List;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.instances.NpcInstance;
import org.apache.commons.lang3.ArrayUtils;

public class SSQLilithMinion
extends Fighter {
    private final int[] dG = new int[]{32719, 32720, 32721};

    public SSQLilithMinion(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.setHasChatWindow(false);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.getInstance().schedule(new Attack(), 3000L);
    }

    private NpcInstance q() {
        List<NpcInstance> list = this.getActor().getAroundNpc(1000, 300);
        if (list != null && !list.isEmpty()) {
            for (NpcInstance npcInstance : list) {
                if (!ArrayUtils.contains(this.dG, npcInstance.getNpcId())) continue;
                return npcInstance;
            }
        }
        return null;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    public class Attack
    extends RunnableImpl {
        @Override
        public void runImpl() {
            if (SSQLilithMinion.this.q() != null) {
                SSQLilithMinion.this.getActor().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, SSQLilithMinion.this.q(), 10000000);
            }
        }
    }
}

