/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Mystic;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class SoulofTreeGuardian
extends Mystic {
    private boolean aZ = true;

    public SoulofTreeGuardian(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtSpawn() {
        this.aZ = true;
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (this.aZ) {
            this.aZ = false;
            if (Rnd.chance(10)) {
                Functions.npcSayInRangeCustomMessage(npcInstance, 500, "scripts.ai.SoulofTreeGuardian.WE_MUST_PROTECT", new Object[0]);
            }
        } else if (Rnd.chance(10)) {
            Functions.npcSayInRangeCustomMessage(npcInstance, 500, "scripts.ai.SoulofTreeGuardian.GET_OUT", new Object[0]);
        }
    }
}

