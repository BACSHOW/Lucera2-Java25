/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class OlMahumGeneral
extends Fighter {
    private boolean aZ = true;

    public OlMahumGeneral(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (this.aZ) {
            this.aZ = false;
            if (Rnd.chance(25)) {
                Functions.npcSay(npcInstance, "We shall see about that!");
            }
        } else if (Rnd.chance(10)) {
            Functions.npcSay(npcInstance, "I will definitely repay this humiliation!");
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.aZ = true;
        super.onEvtDead(creature);
    }
}

