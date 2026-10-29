/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.ai.CharacterAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.Die;
import l2.gameserver.templates.npc.NpcTemplate;

public class DeadManInstance
extends NpcInstance {
    public DeadManInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.setAI(new CharacterAI(this));
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        this.setCurrentHp(0.0, false);
        this.broadcastPacket(new Die(this));
        this.setWalking();
    }

    @Override
    public void reduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    @Override
    public boolean isBlocked() {
        return true;
    }
}

