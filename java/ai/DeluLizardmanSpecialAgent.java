/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Ranger;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class DeluLizardmanSpecialAgent
extends Ranger {
    private boolean aZ = true;

    public DeluLizardmanSpecialAgent(NpcInstance npcInstance) {
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
            if (Rnd.chance(25)) {
                Functions.npcSay(npcInstance, "How dare you interrupt our fight! Hey guys, help!");
            }
        } else if (Rnd.chance(10)) {
            Functions.npcSay(npcInstance, "Hey! Were having a duel here!");
        }
        super.onEvtAttacked(creature, n);
    }
}

