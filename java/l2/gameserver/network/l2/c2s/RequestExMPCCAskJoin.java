/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExAskJoinMPCC;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestExMPCCAskJoin
extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        this._name = this.readS(16);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (player.isProcessingRequest()) {
            player.sendPacket((IStaticPacket)Msg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }
        if (!player.isInParty()) {
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
            return;
        }
        Player player2 = World.getPlayer(this._name);
        if (player2 == null) {
            player.sendPacket((IStaticPacket)Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
            return;
        }
        if (player == player2 || !player2.isInParty() || player.getParty() == player2.getParty()) {
            player.sendPacket((IStaticPacket)Msg.YOU_HAVE_INVITED_WRONG_TARGET);
            return;
        }
        if (player2.isInParty() && !player2.getParty().isLeader(player2)) {
            player2 = player2.getParty().getPartyLeader();
        }
        if (player2 == null) {
            player.sendPacket((IStaticPacket)Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
            return;
        }
        if (player2.getParty().isInCommandChannel()) {
            player.sendPacket((IStaticPacket)new SystemMessage(1594).addString(player2.getName()));
            return;
        }
        if (player2.isBusy()) {
            player.sendPacket((IStaticPacket)new SystemMessage(153).addString(player2.getName()));
            return;
        }
        Party party = player.getParty();
        if (party.isInCommandChannel()) {
            CommandChannel commandChannel = party.getCommandChannel();
            if (commandChannel.getChannelLeader() != player) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
                return;
            }
            if (commandChannel.getParties().size() >= Config.COMMAND_CHANNEL_PARY_COUNT_LIMIT) {
                player.sendPacket((IStaticPacket)SystemMsg.THE_COMMAND_CHANNEL_IS_FULL);
                return;
            }
            this.d(player, player2);
        } else if (CommandChannel.checkAuthority(player)) {
            this.d(player, player2);
        }
    }

    private void d(Player player, Player player2) {
        new Request(Request.L2RequestType.CHANNEL, player, player2).setTimeout(10000L);
        player2.sendPacket((IStaticPacket)new ExAskJoinMPCC(player.getName()));
        player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestExMPCCAskJoin.InviteToCommandChannel", player, new Object[0]).addString(player2.getName()));
    }
}

