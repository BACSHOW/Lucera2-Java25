/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public final class PassagewayMobWithHerbInstance
extends MonsterInstance {
    public static final int FieryDemonBloodHerb = 9849;

    public PassagewayMobWithHerbInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void calculateRewards(Creature creature) {
        if (creature == null) {
            return;
        }
        super.calculateRewards(creature);
        if (creature.isPlayable()) {
            this.dropItem(creature.getPlayer(), 9849, 1L);
        }
    }
}

