/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class FlaggingBossInstance
extends RaidBossInstance {
    public FlaggingBossInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onDeath(Creature creature) {
        List<Player> list = World.getAroundPlayers(this, 2000, 300);
        for (Player player : list) {
            if (player == null) continue;
            player.startPvPFlag(player);
        }
        super.onDeath(creature);
    }
}

