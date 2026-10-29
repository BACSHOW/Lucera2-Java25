/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class ReflectionBossInstance
extends RaidBossInstance {
    private static final int VO = 5;

    public ReflectionBossInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onDeath(Creature creature) {
        this.getMinionList().unspawnMinions();
        super.onDeath(creature);
        this.clearReflection();
    }

    protected void clearReflection() {
        this.getReflection().clearReflection(5, true);
    }
}

