/*
 * Decompiled with CFR 0.152.
 */
package ai.custom;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class MutantChest
extends Fighter {
    public MutantChest(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.startImmobilized();
    }

    @Override
    protected void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        if (Rnd.chance(30)) {
            Functions.npcSay(npcInstance, "\u0412\u0440\u0430\u0433\u0438! \u0412\u0441\u044e\u0434\u0443 \u0432\u0440\u0430\u0433\u0438! \u0412\u0441\u0435 \u0441\u044e\u0434\u0430, \u0432\u0440\u0430\u0433\u0438 \u0437\u0434\u0435\u0441\u044c!");
        }
        npcInstance.deleteMe();
    }
}

