/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;

public class NihilInvaderChest
extends DefaultAI {
    private static int[] cv = new int[]{4039, 4040, 4041, 4042, 4043, 4044};
    private static int[] cw = new int[]{9628, 9629, 9630};

    public NihilInvaderChest(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.startImmobilized();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.getNpcId() == 18820) {
            if (Rnd.chance(40)) {
                npcInstance.broadcastPacket(new MagicSkillUse(npcInstance, npcInstance, 2025, 1, 0, 10L));
                npcInstance.dropItem(creature.getPlayer(), cv[Rnd.get(0, cv.length - 1)], Rnd.get(10, 20));
                npcInstance.doDie(null);
            }
        } else if (npcInstance.getNpcId() == 18823 && Rnd.chance(40)) {
            npcInstance.broadcastPacket(new MagicSkillUse(npcInstance, npcInstance, 2025, 1, 0, 10L));
            npcInstance.dropItem(creature.getPlayer(), cw[Rnd.get(0, cw.length - 1)], Rnd.get(10, 20));
            npcInstance.doDie(null);
        }
        for (NpcInstance npcInstance2 : npcInstance.getReflection().getNpcs()) {
            if (npcInstance2.getNpcId() != npcInstance.getNpcId()) continue;
            npcInstance2.deleteMe();
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}

