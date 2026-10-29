/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;

public class Taurin
extends DefaultAI {
    static final Location[] points = new Location[]{new Location(80752, 146400, -3533), new Location(80250, 146988, -3559), new Location(80070, 146942, -3559), new Location(80048, 146705, -3547), new Location(79784, 146561, -3546), new Location(79476, 146800, -3547), new Location(79490, 147480, -3559), new Location(79812, 148310, -3559), new Location(79692, 148564, -3559), new Location(77569, 148495, -3623), new Location(77495, 148191, -3622), new Location(77569, 148495, -3623), new Location(79819, 148740, -3559), new Location(79773, 149110, -3559), new Location(79291, 149523, -3559), new Location(79569, 150214, -3548), new Location(79679, 150717, -3543), new Location(80106, 150630, -3547), new Location(81207, 150276, -3559), new Location(81820, 150666, -3559), new Location(82038, 150589, -3559), new Location(82394, 149943, -3559), new Location(82038, 150589, -3559), new Location(81820, 150666, -3559), new Location(81582, 150590, -3559), new Location(81535, 149653, -3495), new Location(83814, 148630, -3420), new Location(87001, 148637, -3428), new Location(83814, 148630, -3420), new Location(82921, 148467, -3495), new Location(82060, 148070, -3495), new Location(82060, 148070, -3495), new Location(82060, 148070, -3495), new Location(82060, 148070, -3495), new Location(81544, 147514, -3491), new Location(81691, 146578, -3559), new Location(83190, 146687, -3491), new Location(81691, 146578, -3559), new Location(81331, 146915, -3559), new Location(81067, 146925, -3559), new Location(80752, 146400, -3533)};
    private int bx = -1;
    private long by = 0L;
    private boolean bz = false;

    public Taurin(NpcInstance npcInstance) {
        super(npcInstance);
        this.AI_TASK_ATTACK_DELAY = 250L;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return true;
        }
        if (this._def_think) {
            this.doTask();
            return true;
        }
        if (System.currentTimeMillis() > this.by && (this.bx > -1 || Rnd.chance(5))) {
            if (!this.bz) {
                switch (this.bx) {
                    case 4: {
                        this.by = System.currentTimeMillis() + 10000L;
                        this.bz = true;
                        return true;
                    }
                    case 10: {
                        this.by = System.currentTimeMillis() + 10000L;
                        this.bz = true;
                        return true;
                    }
                    case 14: {
                        this.by = System.currentTimeMillis() + 10000L;
                        this.bz = true;
                        return true;
                    }
                    case 16: {
                        this.by = System.currentTimeMillis() + 10000L;
                        this.bz = true;
                        return true;
                    }
                    case 21: {
                        this.by = System.currentTimeMillis() + 10000L;
                        this.bz = true;
                        return true;
                    }
                    case 27: {
                        this.by = System.currentTimeMillis() + 10000L;
                        this.bz = true;
                        return true;
                    }
                    case 30: {
                        this.by = System.currentTimeMillis() + 10000L;
                        Functions.npcSay(npcInstance, "\u0412\u0441\u0435\u043c \u043b\u0435\u0436\u0430\u0442\u044c, \u0443 \u043c\u0435\u043d\u044f \u0431\u043e\u043c\u0431\u0430!");
                        this.bz = true;
                        return true;
                    }
                    case 31: {
                        this.by = System.currentTimeMillis() + 15000L;
                        Functions.npcSay(npcInstance, "\u042f \u0431\u043e\u043b\u044c\u043d\u043e\u0439, \u0437\u0430 \u0441\u0435\u0431\u044f \u043d\u0435 \u0440\u0443\u0447\u0430\u044e\u0441\u044c!!!!");
                        this.bz = true;
                        return true;
                    }
                    case 32: {
                        this.by = System.currentTimeMillis() + 15000L;
                        Functions.npcSay(npcInstance, "\u0412\u044b \u0432\u0441\u0435 \u0435\u0449\u0435 \u0442\u0443\u0442? \u042f \u0432\u0430\u0441 \u043f\u0440\u0435\u0434\u0443\u043f\u0440\u0435\u0436\u0434\u0430\u043b!!!!!");
                        this.bz = true;
                        return true;
                    }
                    case 33: {
                        npcInstance.broadcastPacket(new MagicSkillUse(npcInstance, npcInstance, 2025, 1, 500, 0L));
                        this.by = System.currentTimeMillis() + 1000L;
                        this.bz = true;
                        return true;
                    }
                    case 35: {
                        this.by = System.currentTimeMillis() + 10000L;
                        this.bz = true;
                        return true;
                    }
                    case 37: {
                        this.by = System.currentTimeMillis() + 30000000L;
                        this.bz = true;
                        return true;
                    }
                }
            }
            this.by = 0L;
            this.bz = false;
            ++this.bx;
            if (this.bx >= points.length) {
                this.bx = 0;
            }
            this.addTaskMove(points[this.bx], true);
            this.doTask();
            return true;
        }
        return this.randomAnimation();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}

