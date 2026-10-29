/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public class Elpy
extends Fighter {
    public Elpy(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (creature != null && Rnd.chance(50)) {
            Location location = Location.findPointToStay(npcInstance, 150, 200);
            if (GeoEngine.canMoveToCoord(npcInstance.getX(), npcInstance.getY(), npcInstance.getZ(), location.x, location.y, location.z, npcInstance.getGeoIndex())) {
                npcInstance.setRunning();
                this.addTaskMove(location, false);
            }
        }
    }

    @Override
    public boolean checkAggression(Creature creature) {
        return false;
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}

