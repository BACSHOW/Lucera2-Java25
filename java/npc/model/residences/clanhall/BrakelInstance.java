/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.TimeUtils;

public class BrakelInstance
extends NpcInstance {
    public BrakelInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        ClanHall clanHall = ResidenceHolder.getInstance().getResidence(ClanHall.class, 21);
        if (clanHall == null) {
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile("residence2/clanhall/partisan_ordery_brakel001.htm");
        npcHtmlMessage.replace("%next_siege%", TimeUtils.toSimpleFormat(clanHall.getSiegeDate().getTimeInMillis()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }
}

