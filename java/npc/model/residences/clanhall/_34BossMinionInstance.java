/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import l2.gameserver.model.Creature;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.SiegeGuardInstance;
import npc.model.residences.clanhall._34SiegeGuard;

public abstract class _34BossMinionInstance
extends SiegeGuardInstance
implements _34SiegeGuard {
    public _34BossMinionInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onDeath(Creature creature) {
        this.setCurrentHp(1.0, true);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        Functions.npcShout(this, this.spawnChatSay());
    }

    public abstract String spawnChatSay();

    @Override
    public abstract String teleChatSay();
}

