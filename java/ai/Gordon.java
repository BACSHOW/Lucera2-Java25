/*
 * Decompiled with CFR 0.152.
 */
package ai;

import ai.moveroute.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class Gordon
extends Fighter {
    public Gordon(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public boolean isGlobalAI() {
        return false;
    }

    @Override
    public boolean checkAggression(Creature creature) {
        if (!creature.isPlayable()) {
            return false;
        }
        if (!creature.isCursedWeaponEquipped()) {
            return false;
        }
        return super.checkAggression(creature);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}

