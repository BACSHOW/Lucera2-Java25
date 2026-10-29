/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestExOustFromMPCC
extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        this._name = this.readS(16);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || !player.isInParty() || !player.getParty().isInCommandChannel()) {
            return;
        }
        Player player2 = World.getPlayer(this._name);
        if (player2 == null) {
            player.sendPacket((IStaticPacket)Msg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
            return;
        }
        if (player == player2) {
            return;
        }
        if (!player2.isInParty() || !player2.getParty().isInCommandChannel() || player.getParty().getCommandChannel() != player2.getParty().getCommandChannel()) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        if (player.getParty().getCommandChannel().getChannelLeader() != player) {
            player.sendPacket((IStaticPacket)Msg.ONLY_THE_CREATOR_OF_A_CHANNEL_CAN_ISSUE_A_GLOBAL_COMMAND);
            return;
        }
        player2.getParty().getCommandChannel().getChannelLeader().sendPacket((IStaticPacket)new SystemMessage(1584).addString(player2.getName()));
        player2.getParty().getCommandChannel().removeParty(player2.getParty());
        player2.getParty().broadCast(Msg.YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL);
    }
}

