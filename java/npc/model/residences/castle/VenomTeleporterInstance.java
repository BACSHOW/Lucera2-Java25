/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class VenomTeleporterInstance
extends NpcInstance {
    public VenomTeleporterInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        Castle castle = this.getCastle();
        if (((SiegeEvent)castle.getSiegeEvent()).isInProgress()) {
            this.showChatWindow(player, "residence2/castle/rune_massymore_teleporter002.htm", new Object[0]);
        } else {
            player.teleToLocation(12589, -49044, -3008);
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        this.showChatWindow(player, "residence2/castle/rune_massymore_teleporter001.htm", new Object[0]);
    }
}

