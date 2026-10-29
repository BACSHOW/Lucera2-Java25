/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;

public class EdwinFollower
extends DefaultAI {
    private static final int bB = 32072;
    private static final int bC = 200;
    private long bf = 0L;
    private HardReference<? extends Creature> bD = HardReferences.emptyRef();

    public EdwinFollower(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        Creature creature = this.bD.get();
        if (creature == null) {
            if (System.currentTimeMillis() > this.bf) {
                this.bf = System.currentTimeMillis() + 15000L;
                for (NpcInstance npcInstance2 : World.getAroundNpc(npcInstance)) {
                    if (npcInstance2.getNpcId() != 32072) continue;
                    this.bD = npcInstance2.getRef();
                    return true;
                }
            }
        } else if (!npcInstance.isMoving()) {
            int n = creature.getX() + Rnd.get(400) - 200;
            int n2 = creature.getY() + Rnd.get(400) - 200;
            int n3 = creature.getZ();
            npcInstance.setRunning();
            npcInstance.moveToLocation(n, n2, n3, 0, true);
            return true;
        }
        return false;
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}

