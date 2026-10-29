/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class RequestExMPCCAcceptJoin
extends L2GameClientPacket {
    private int py;
    private int abR;

    @Override
    protected void readImpl() {
        this.py = this._buf.hasRemaining() ? this.readD() : 0;
        this.abR = this._buf.hasRemaining() ? this.readD() : 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        CommandChannel commandChannel;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Request request = player.getRequest();
        if (request == null || !request.isTypeOf(Request.L2RequestType.CHANNEL)) {
            return;
        }
        if (!request.isInProgress()) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        if (player.isOutOfControl()) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        Player player2 = request.getRequestor();
        if (player2 == null) {
            request.cancel();
            player.sendPacket((IStaticPacket)Msg.THAT_PLAYER_IS_NOT_ONLINE);
            player.sendActionFailed();
            return;
        }
        if (player2.getRequest() != request) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        if (this.py == 0) {
            request.cancel();
            player2.sendPacket((IStaticPacket)new SystemMessage(1680).addString(player.getName()));
            return;
        }
        if (!player2.isInParty() || !player.isInParty() || player.getParty().isInCommandChannel()) {
            request.cancel();
            player2.sendPacket((IStaticPacket)Msg.NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL);
            return;
        }
        if (player.isTeleporting()) {
            request.cancel();
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_JOIN_A_COMMAND_CHANNEL_WHILE_TELEPORTING);
            player2.sendPacket((IStaticPacket)Msg.NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL);
            return;
        }
        if (player2.getParty().isInCommandChannel() && (commandChannel = player2.getParty().getCommandChannel()).getParties().size() >= Config.COMMAND_CHANNEL_PARY_COUNT_LIMIT) {
            player2.sendPacket((IStaticPacket)SystemMsg.THE_COMMAND_CHANNEL_IS_FULL);
            request.cancel();
            return;
        }
        if (player2.getParty().isInCommandChannel()) {
            try {
                player2.getParty().getCommandChannel().addParty(player.getParty());
            }
            finally {
                request.done();
            }
        }
        if (CommandChannel.checkAuthority(player2)) {
            boolean bl = player2.getSkillLevel(391) > 0;
            boolean bl2 = false;
            if (!bl) {
                boolean bl3 = bl2 = player2.getInventory().getCountOf(8871) > 1L;
            }
            if (!bl && !bl2) {
                player2.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                return;
            }
            try {
                if (!bl && (bl2 = player2.getInventory().destroyItemByItemId(8871, 1L))) {
                    player2.sendPacket((IStaticPacket)SystemMessage2.removeItems(8871, 1L));
                }
                if (!bl && !bl2) {
                    return;
                }
                CommandChannel commandChannel2 = new CommandChannel(player2);
                player2.sendPacket((IStaticPacket)Msg.THE_COMMAND_CHANNEL_HAS_BEEN_FORMED);
                commandChannel2.addParty(player.getParty());
            }
            finally {
                request.done();
            }
        }
    }
}

