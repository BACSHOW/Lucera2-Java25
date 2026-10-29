/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.HashMap;
import java.util.Map;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.NpcUtils;

public class FatesWhisperBoss
extends Fighter {
    private static final Map<Integer, Integer> bM = new HashMap<Integer, Integer>();

    public FatesWhisperBoss(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        Integer n = bM.get(npcInstance.getNpcId());
        if (n != null) {
            NpcUtils.spawnSingle((int)n, npcInstance.getLoc(), 120000L);
        }
        super.onEvtDead(creature);
    }

    static {
        bM.put(25035, 31027);
        bM.put(25054, 31028);
        bM.put(25126, 31029);
        bM.put(25220, 31030);
    }
}

