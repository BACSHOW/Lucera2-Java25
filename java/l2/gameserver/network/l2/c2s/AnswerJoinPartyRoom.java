/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;

public class AnswerJoinPartyRoom
extends L2GameClientPacket {
    private int py;

    @Override
    protected void readImpl() {
        this.py = this._buf.hasRemaining() ? this.readD() : 0;
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
        if (request == null || !request.isTypeOf(Request.L2RequestType.PARTY_ROOM)) {
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
            player.sendPacket((IStaticPacket)SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
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
            player2.sendPacket((IStaticPacket)SystemMsg.THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY);
            return;
        }
        if (player.getMatchingRoom() != null) {
            request.cancel();
            player.sendActionFailed();
            return;
        }
        try {
            MatchingRoom matchingRoom = player2.getMatchingRoom();
            if (matchingRoom == null || matchingRoom.getType() != MatchingRoom.PARTY_MATCHING) {
                return;
            }
            matchingRoom.addMember(player);
        }
        finally {
            request.done();
        }
    }
}

