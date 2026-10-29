/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.ai;

import l2.gameserver.ai.CharacterAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.entity.boat.Boat;

public class BoatAI
extends CharacterAI {
    public BoatAI(Creature creature) {
        super(creature);
    }

    @Override
    protected void onEvtArrived() {
        Boat boat = (Boat)this.getActor();
        if (boat == null) {
            return;
        }
        boat.onEvtArrived();
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}

