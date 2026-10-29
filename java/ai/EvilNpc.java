/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;

public class EvilNpc
extends DefaultAI {
    private long bK;
    private static final String[] bL = new String[]{"\u043e\u0442\u0441\u0442\u0430\u043d\u044c!", "\u0443\u0439\u043c\u0438\u0441\u044c!", "\u044f \u0442\u0435\u0431\u0435 \u043e\u0442\u043e\u043c\u0449\u0443, \u043f\u043e\u0442\u043e\u043c \u0431\u0443\u0434\u0435\u0448\u044c \u043f\u0440\u043e\u0449\u0435\u043d\u0438\u044f \u043f\u0440\u043e\u0441\u0438\u0442\u044c!", "\u0443 \u0442\u0435\u0431\u044f \u0431\u0443\u0434\u0443\u0442 \u043d\u0435\u043f\u0440\u0438\u044f\u0442\u043d\u043e\u0441\u0442\u0438!", "\u044f \u043d\u0430 \u0442\u0435\u0431\u044f \u043f\u043e\u0436\u0430\u043b\u0443\u044e\u0441\u044c, \u0442\u0435\u0431\u044f \u0430\u0440\u0435\u0441\u0442\u0443\u044e\u0442!"};

    public EvilNpc(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature == null || creature.getPlayer() == null) {
            return;
        }
        if (System.currentTimeMillis() - this.bK > 3000L) {
            int n2 = Rnd.get(0, 100);
            if (n2 < 2) {
                creature.getPlayer().setKarma(creature.getPlayer().getKarma() + 5, true);
            } else if (n2 < 4) {
                npcInstance.doCast(SkillTable.getInstance().getInfo(4578, 1), creature, true);
            } else {
                npcInstance.doCast(SkillTable.getInstance().getInfo(4185, 7), creature, true);
            }
            Functions.npcSay(npcInstance, creature.getName() + ", " + bL[Rnd.get(bL.length)]);
            this.bK = System.currentTimeMillis();
        }
    }
}

