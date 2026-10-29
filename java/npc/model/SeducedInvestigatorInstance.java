/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;

public class SeducedInvestigatorInstance
extends MonsterInstance {
    public SeducedInvestigatorInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.setHasChatWindow(true);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, "common/seducedinvestigator.htm", n));
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        Player player = creature.getPlayer();
        if (player == null) {
            return false;
        }
        return !player.isPlayable();
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}

