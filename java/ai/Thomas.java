/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class Thomas
extends Fighter {
    private long cX;
    private static final String[] cY = new String[]{"\u0425\u0430...\u0425\u0430... \u0412\u044b \u043f\u0440\u0438\u0448\u043b\u0438 \u0441\u043f\u0430\u0441\u0442\u0438 \u0441\u043d\u0435\u0433\u043e\u0432\u0438\u043a\u0430?", "\u0422\u0430\u043a \u043f\u0440\u043e\u0441\u0442\u043e \u044f \u0432\u0430\u043c \u0435\u0433\u043e \u043d\u0435 \u043e\u0442\u0434\u0430\u043c!", "\u0427\u0442\u043e\u0431\u044b \u0441\u043f\u0430\u0441\u0442\u0438 \u0432\u0430\u0448\u0435\u0433\u043e \u0441\u043d\u0435\u0433\u043e\u0432\u0438\u043a\u0430, \u0432\u0430\u043c \u043f\u0440\u0438\u0434\u0435\u0442\u0441\u044f \u0443\u0431\u0438\u0442\u044c \u043c\u0435\u043d\u044f!", "\u0425\u0430...\u0425\u0430... \u0412\u044b \u0434\u0443\u043c\u0430\u0435\u0442\u0435 \u044d\u0442\u043e \u0442\u0430\u043a \u043f\u0440\u043e\u0441\u0442\u043e?"};
    private static final String[] cZ = new String[]{"\u0412\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0432\u0441\u0435 \u0443\u043c\u0435\u0440\u0435\u0442\u044c!", "\u0421\u043d\u0435\u0433\u043e\u0432\u0438\u043a \u043c\u043e\u0439 \u0438 \u043d\u0435 \u0431\u0443\u0434\u0435\u0442 \u0443 \u0432\u0430\u0441 \u041d\u043e\u0432\u043e\u0433\u043e \u0413\u043e\u0434\u0430!", "\u042f \u0432\u0430\u0441 \u0432\u0441\u0435\u0445 \u0443\u0431\u044c\u044e!", "\u0427\u0442\u043e \u0442\u0430\u043a \u0441\u043b\u0430\u0431\u043e \u0431\u044c\u0435\u0442\u0435? \u041c\u0430\u043b\u043e \u043a\u0430\u0448\u0438 \u0435\u043b\u0438? \u0425\u0430... \u0425\u0430...", "\u0418 \u044d\u0442\u043e \u043d\u0430\u0437\u044b\u0432\u0430\u0435\u0442\u0441\u044f \u0433\u0435\u0440\u043e\u0438?", "\u041d\u0435 \u0432\u0438\u0434\u0430\u0442\u044c \u0432\u0430\u043c \u0441\u043d\u0435\u0433\u043e\u0432\u0438\u043a\u0430!", "\u0422\u043e\u043b\u044c\u043a\u043e \u0434\u0440\u0435\u0432\u043d\u0435\u0435 \u043e\u0440\u0443\u0436\u0438\u0435 \u0441\u043f\u043e\u0441\u043e\u0431\u043d\u043e \u043f\u043e\u0431\u0435\u0434\u0438\u0442\u044c \u043c\u0435\u043d\u044f!"};

    public Thomas(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return true;
        }
        if (!npcInstance.isInCombat() && System.currentTimeMillis() - this.cX > 10000L) {
            Functions.npcSay(npcInstance, cY[Rnd.get(cY.length)]);
            this.cX = System.currentTimeMillis();
        }
        return super.thinkActive();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature == null || creature.getPlayer() == null) {
            return;
        }
        if (System.currentTimeMillis() - this.cX > 5000L) {
            Functions.npcSay(npcInstance, cZ[Rnd.get(cZ.length)]);
            this.cX = System.currentTimeMillis();
        }
        super.onEvtAttacked(creature, n);
    }
}

