/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;

public class EvasGiftBox
extends Fighter {
    private static final int[] bH = new int[]{1073, 3141, 3252};
    private static final int bI = 9692;
    private static final int bJ = 9693;

    public EvasGiftBox(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        Player player;
        NpcInstance npcInstance = this.getActor();
        if (creature != null && (player = creature.getPlayer()) != null && player.getEffectList().containEffectFromSkills(bH)) {
            npcInstance.dropItem(player, Rnd.chance(50) ? 9692 : 9693, 1L);
        }
        super.onEvtDead(creature);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}

