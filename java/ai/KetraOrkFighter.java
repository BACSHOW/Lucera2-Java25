/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class KetraOrkFighter
extends Fighter {
    public KetraOrkFighter(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public boolean checkAggression(Creature creature) {
        if (!creature.isPlayable()) {
            return false;
        }
        if (creature.getPlayer().getInventory().getCountOf(7211) > 0L || creature.getPlayer().getInventory().getCountOf(7212) > 0L || creature.getPlayer().getInventory().getCountOf(7213) > 0L || creature.getPlayer().getInventory().getCountOf(7214) > 0L || creature.getPlayer().getInventory().getCountOf(7215) > 0L) {
            return false;
        }
        return super.checkAggression(creature);
    }
}

