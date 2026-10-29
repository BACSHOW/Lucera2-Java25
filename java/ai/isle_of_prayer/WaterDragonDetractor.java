/*
 * Decompiled with CFR 0.152.
 */
package ai.isle_of_prayer;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;

public class WaterDragonDetractor
extends Fighter {
    private static final int eg = 9689;
    private static final int ef = 9595;

    public WaterDragonDetractor(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        Player player;
        if (creature != null && (player = creature.getPlayer()) != null) {
            NpcInstance npcInstance = this.getActor();
            npcInstance.dropItem(player, 9689, 1L);
            if (Rnd.chance(10)) {
                npcInstance.dropItem(player, 9595, 1L);
            }
        }
        super.onEvtDead(creature);
    }
}

