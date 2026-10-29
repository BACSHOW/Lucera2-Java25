/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import l2.gameserver.ai.DefaultAI;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Ranger
extends DefaultAI {
    public Ranger(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        return super.thinkActive() || this.defaultThinkBuff(10);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        super.onEvtAttacked(creature, n);
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead() || creature == null || npcInstance.getDistance(creature) > 200.0) {
            return;
        }
        if (npcInstance.isMoving()) {
            return;
        }
        int n2 = npcInstance.getX();
        int n3 = npcInstance.getY();
        int n4 = npcInstance.getZ();
        int n5 = n2;
        int n6 = n3;
        int n7 = n4;
        int n8 = n2 < creature.getX() ? -1 : 1;
        int n9 = n3 < creature.getY() ? -1 : 1;
        int n10 = (int)(0.71 * (double)npcInstance.calculateAttackDelay() / 1000.0 * (double)npcInstance.getMoveSpeed());
        n4 = GeoEngine.getHeight(n2 += n8 * n10, n3 += n9 * n10, n4, npcInstance.getGeoIndex());
        if (GeoEngine.canMoveToCoord(n5, n6, n7, n2, n3, n4, npcInstance.getGeoIndex())) {
            this.addTaskMove(n2, n3, n4, false);
            this.addTaskAttack(creature);
        }
    }

    @Override
    protected boolean createNewTask() {
        return this.defaultFightTask();
    }

    @Override
    public int getRatePHYS() {
        return 10;
    }

    @Override
    public int getRateDOT() {
        return 15;
    }

    @Override
    public int getRateDEBUFF() {
        return 8;
    }

    @Override
    public int getRateDAM() {
        return 20;
    }

    @Override
    public int getRateSTUN() {
        return 15;
    }

    @Override
    public int getRateBUFF() {
        return 3;
    }

    @Override
    public int getRateHEAL() {
        return 20;
    }
}

