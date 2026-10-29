/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExAskJoinPartyRoom;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class RequestAskJoinPartyRoom
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
        Player player2 = World.getPlayer(this._name);
        if (player2 == null || player2 == player) {
            player.sendActionFailed();
            return;
        }
        if (player.isProcessingRequest()) {
            player.sendPacket((IStaticPacket)SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }
        if (player2.isProcessingRequest()) {
            player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(player2));
            return;
        }
        if (player2.getMatchingRoom() != null) {
            return;
        }
        MatchingRoom matchingRoom = player.getMatchingRoom();
        if (matchingRoom == null || matchingRoom.getType() != MatchingRoom.PARTY_MATCHING) {
            return;
        }
        if (matchingRoom.getLeader() != player) {
            player.sendPacket((IStaticPacket)SystemMsg.ONLY_A_ROOM_LEADER_MAY_INVITE_OTHERS_TO_A_PARTY_ROOM);
            return;
        }
        if (matchingRoom.getPlayers().size() >= matchingRoom.getMaxMembersSize()) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_PARTY_ROOM_IS_FULL);
            return;
        }
        new Request(Request.L2RequestType.PARTY_ROOM, player, player2).setTimeout(10000L);
        player2.sendPacket((IStaticPacket)new ExAskJoinPartyRoom(player.getName(), matchingRoom.getTopic()));
        player.sendPacket((IStaticPacket)((SystemMessage2)new SystemMessage2(SystemMsg.S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2).addName(player)).addString(matchingRoom.getTopic()));
        player2.sendPacket((IStaticPacket)((SystemMessage2)new SystemMessage2(SystemMsg.S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2).addName(player)).addString(matchingRoom.getTopic()));
    }
}

