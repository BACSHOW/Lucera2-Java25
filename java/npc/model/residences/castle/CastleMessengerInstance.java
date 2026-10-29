/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.CastleSiegeInfo;
import l2.gameserver.templates.npc.NpcTemplate;

public class CastleMessengerInstance
extends NpcInstance {
    public CastleMessengerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        Castle castle = this.getCastle();
        if (player.isCastleLord(castle.getId())) {
            if (((SiegeEvent)castle.getSiegeEvent()).isInProgress()) {
                this.showChatWindow(player, "residence2/castle/sir_tyron021.htm", new Object[0]);
            } else {
                this.showChatWindow(player, "residence2/castle/sir_tyron007.htm", new Object[0]);
            }
        } else if (((SiegeEvent)castle.getSiegeEvent()).isInProgress()) {
            this.showChatWindow(player, "residence2/castle/sir_tyron021.htm", new Object[0]);
        } else {
            player.sendPacket((IStaticPacket)new CastleSiegeInfo(castle, player));
        }
    }
}

