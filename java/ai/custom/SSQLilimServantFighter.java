/*
 * Decompiled with CFR 0.152.
 */
package ai.custom;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class SSQLilimServantFighter
extends Fighter {
    private boolean dH = false;

    public SSQLilimServantFighter(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        if (Rnd.chance(30) && !this.dH) {
            Functions.npcSay(this.getActor(), Rnd.chance(50) ? "Those who are afraid should get away and those who are brave should fight!" : "This place once belonged to Lord Shilen.");
            this.dH = true;
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        if (Rnd.chance(30)) {
            Functions.npcSay(this.getActor(), Rnd.chance(50) ? "Why are you getting in our way?" : "Shilen... our Shilen!");
        }
        super.onEvtDead(creature);
    }
}

