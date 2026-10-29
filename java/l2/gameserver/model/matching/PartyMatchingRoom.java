/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.matching;

import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExClosePartyRoom;
import l2.gameserver.network.l2.s2c.ExPartyRoomMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PartyRoomInfo;

public class PartyMatchingRoom
extends MatchingRoom {
    public PartyMatchingRoom(Player player, int n, int n2, int n3, int n4, String string) {
        super(player, n, n2, n3, n4, string);
        player.broadcastCharInfo();
    }

    @Override
    public SystemMsg notValidMessage() {
        return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM;
    }

    @Override
    public SystemMsg enterMessage() {
        return SystemMsg.C1_HAS_ENTERED_THE_PARTY_ROOM;
    }

    @Override
    public SystemMsg exitMessage(boolean bl, boolean bl2) {
        if (bl) {
            return bl2 ? SystemMsg.C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM : SystemMsg.C1_HAS_LEFT_THE_PARTY_ROOM;
        }
        return bl2 ? SystemMsg.YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM : SystemMsg.YOU_HAVE_EXITED_THE_PARTY_ROOM;
    }

    @Override
    public SystemMsg closeRoomMessage() {
        return SystemMsg.THE_PARTY_ROOM_HAS_BEEN_DISBANDED;
    }

    @Override
    public L2GameServerPacket closeRoomPacket() {
        return ExClosePartyRoom.STATIC;
    }

    @Override
    public L2GameServerPacket infoRoomPacket() {
        return new PartyRoomInfo(this);
    }

    @Override
    public L2GameServerPacket addMemberPacket(Player player, Player player2) {
        return this.membersPacket(player);
    }

    @Override
    public L2GameServerPacket removeMemberPacket(Player player, Player player2) {
        return this.membersPacket(player);
    }

    @Override
    public L2GameServerPacket updateMemberPacket(Player player, Player player2) {
        return this.membersPacket(player);
    }

    @Override
    public L2GameServerPacket membersPacket(Player player) {
        return new ExPartyRoomMember(this, player);
    }

    @Override
    public int getType() {
        return PARTY_MATCHING;
    }

    @Override
    public int getMemberType(Player player) {
        return player.equals(this._leader) ? ROOM_MASTER : (player.getParty() != null && this._leader.getParty() == player.getParty() ? PARTY_MEMBER : WAIT_PLAYER);
    }
}

