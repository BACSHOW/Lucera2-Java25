/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;

public class Aenkinel
extends Fighter {
    public Aenkinel(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.getNpcId() == 25694 || npcInstance.getNpcId() == 25695) {
            Reflection reflection = npcInstance.getReflection();
            reflection.setReenterTime(System.currentTimeMillis());
        }
        if (npcInstance.getNpcId() == 25694) {
            for (int i = 0; i < 4; ++i) {
                npcInstance.getReflection().addSpawnWithoutRespawn(18820, npcInstance.getLoc(), 250);
            }
        } else if (npcInstance.getNpcId() == 25695) {
            for (int i = 0; i < 4; ++i) {
                npcInstance.getReflection().addSpawnWithoutRespawn(18823, npcInstance.getLoc(), 250);
            }
        }
        super.onEvtDead(creature);
    }
}

