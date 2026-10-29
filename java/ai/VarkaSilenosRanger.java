/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Ranger;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;

public class VarkaSilenosRanger
extends Ranger {
    public VarkaSilenosRanger(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public boolean checkAggression(Creature creature) {
        if (!creature.isPlayable()) {
            return false;
        }
        if (creature.getPlayer().getInventory().getCountOf(7221) > 0L || creature.getPlayer().getInventory().getCountOf(7222) > 0L || creature.getPlayer().getInventory().getCountOf(7223) > 0L || creature.getPlayer().getInventory().getCountOf(7224) > 0L || creature.getPlayer().getInventory().getCountOf(7225) > 0L) {
            return false;
        }
        return super.checkAggression(creature);
    }
}

