/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class GuardianAngel
extends DefaultAI {
    static final String[] flood = new String[]{"Waaaah! Step back from the confounded box! I will take it myself!", "Grr! Who are you and why have you stopped me?", "Grr. I've been hit..."};

    public GuardianAngel(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        Functions.npcSay(npcInstance, flood[Rnd.get(2)]);
        return super.thinkActive();
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance != null) {
            Functions.npcSay(npcInstance, flood[2]);
        }
        super.onEvtDead(creature);
    }
}

