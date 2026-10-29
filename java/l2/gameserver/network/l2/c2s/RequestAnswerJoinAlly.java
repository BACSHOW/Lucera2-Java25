/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;

public class RequestAnswerJoinAlly
extends L2GameClientPacket {
    private int py;

    @Override
    protected void readImpl() {
        this.py = this._buf.remaining() >= 4 ? this.readD() : 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Request request = player.getRequest();
        if (request == null || !request.isTypeOf(Request.L2RequestType.ALLY)) {
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
        if (player2.getAlliance() == null) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        if (this.py == 0) {
            request.cancel();
            player2.sendPacket((IStaticPacket)Msg.YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE);
            return;
        }
        try {
            Alliance alliance = player2.getAlliance();
            player.sendPacket((IStaticPacket)Msg.YOU_HAVE_ACCEPTED_THE_ALLIANCE);
            player.getClan().setAllyId(player2.getAllyId());
            player.getClan().updateClanInDB();
            alliance.addAllyMember(player.getClan(), true);
            alliance.broadcastAllyStatus();
        }
        finally {
            request.done();
        }
    }
}

