/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public class RndWalkAndAnim
extends DefaultAI {
    protected static final int PET_WALK_RANGE = 100;

    public RndWalkAndAnim(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isMoving()) {
            return false;
        }
        int n = Rnd.get(100);
        if (n < 10) {
            this.randomWalk();
        } else if (n < 20) {
            npcInstance.onRandomAnimation();
        }
        return false;
    }

    @Override
    protected boolean randomWalk() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null) {
            return false;
        }
        Location location = npcInstance.getSpawnedLoc();
        int n = location.x + Rnd.get(200) - 100;
        int n2 = location.y + Rnd.get(200) - 100;
        int n3 = GeoEngine.getHeight(n, n2, location.z, npcInstance.getGeoIndex());
        npcInstance.setRunning();
        npcInstance.moveToLocation(n, n2, n3, 0, true);
        return true;
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}

