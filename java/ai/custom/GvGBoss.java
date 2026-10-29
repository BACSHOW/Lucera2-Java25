/*
 * Decompiled with CFR 0.152.
 */
package ai.custom;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class GvGBoss
extends Fighter {
    boolean phrase1 = false;
    boolean phrase2 = false;
    boolean phrase3 = false;

    public GvGBoss(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.startImmobilized();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.getCurrentHpPercents() < 50.0 && !this.phrase1) {
            this.phrase1 = true;
            Functions.npcSay(npcInstance, "\u0412\u0430\u043c \u043d\u0435 \u0443\u0434\u0430\u0441\u0442\u0441\u044f \u043f\u043e\u0445\u0438\u0442\u0438\u0442\u044c \u0441\u043e\u043a\u0440\u043e\u0432\u0438\u0449\u0430 \u0413\u0435\u0440\u0430\u043b\u044c\u0434\u0430!");
        } else if (npcInstance.getCurrentHpPercents() < 30.0 && !this.phrase2) {
            this.phrase2 = true;
            Functions.npcSay(npcInstance, "\u042f \u0442\u0435\u0431\u0435 \u0447\u0435\u0440\u0435\u043f \u043f\u0440\u043e\u043b\u043e\u043c\u043b\u044e!");
        } else if (npcInstance.getCurrentHpPercents() < 5.0 && !this.phrase3) {
            this.phrase3 = true;
            Functions.npcSay(npcInstance, "\u0412\u044b \u0432\u0441\u0435 \u043f\u043e\u0433\u0438\u0431\u043d\u0435\u0442\u0435 \u0432 \u0441\u0442\u0440\u0430\u0448\u043d\u044b\u0445 \u043c\u0443\u043a\u0430\u0445! \u0423\u043d\u0438\u0447\u0442\u043e\u0436\u0443!");
        }
        super.onEvtAttacked(creature, n);
    }
}

