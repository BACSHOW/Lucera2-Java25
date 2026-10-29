/*
 * Decompiled with CFR 0.152.
 */
package handler.usercommands;

import handler.usercommands.ScriptUserCommand;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

public class SiegeStatus
extends ScriptUserCommand {
    public static final int[] COMMANDS = new int[]{99};

    @Override
    public boolean useUserCommand(int n, Player player) {
        if (!player.isClanLeader()) {
            player.sendPacket((IStaticPacket)SystemMsg.ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS);
            return false;
        }
        Castle castle = player.getCastle();
        if (castle == null) {
            return false;
        }
        if (((SiegeEvent)castle.getSiegeEvent()).isInProgress() && !player.isNoble()) {
            player.sendPacket((IStaticPacket)SystemMsg.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR);
            return false;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        npcHtmlMessage.setFile("siege_status.htm");
        npcHtmlMessage.replace("%name%", player.getName());
        npcHtmlMessage.replace("%kills%", String.valueOf(0));
        npcHtmlMessage.replace("%deaths%", String.valueOf(0));
        npcHtmlMessage.replace("%type%", String.valueOf(0));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        return true;
    }

    @Override
    public int[] getUserCommandList() {
        return COMMANDS;
    }
}

