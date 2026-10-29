/*
 * Decompiled with CFR 0.152.
 */
package ai.custom;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Mystic;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class SSQLilimServantMage
extends Mystic {
    private boolean dH = false;

    public SSQLilimServantMage(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        super.onEvtAttacked(creature, n);
        if (Rnd.chance(30) && !this.dH) {
            Functions.npcSay(this.getActor(), "Who dares enter this place?");
            this.dH = true;
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        if (Rnd.chance(30)) {
            Functions.npcSay(this.getActor(), "Lord Shilen... some day... you will accomplish... this mission...");
        }
        super.onEvtDead(creature);
    }
}

