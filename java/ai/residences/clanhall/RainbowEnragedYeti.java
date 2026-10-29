/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.clanhall;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class RainbowEnragedYeti
extends Fighter {
    public RainbowEnragedYeti(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        Functions.npcShoutCustomMessage(this.getActor(), "clanhall.RainbowEnragedYeti.OOOH_WHO_POURED_NECTAR_ON_MY_HEAD_WHILE_I_WAS_SLEEPING", new Object[0]);
    }
}

