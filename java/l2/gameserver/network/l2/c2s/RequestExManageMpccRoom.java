/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;

public class RequestExManageMpccRoom
extends L2GameClientPacket {
    private int _id;
    private int abS;
    private int p;
    private int s;
    private String Yl;

    @Override
    protected void readImpl() {
        this._id = this.readD();
        this.abS = this.readD();
        this.p = this.readD();
        this.s = this.readD();
        this.readD();
        this.Yl = this.readS();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        MatchingRoom matchingRoom = player.getMatchingRoom();
        if (matchingRoom == null || matchingRoom.getId() != this._id || matchingRoom.getType() != MatchingRoom.CC_MATCHING) {
            return;
        }
        if (matchingRoom.getLeader() != player) {
            return;
        }
        matchingRoom.setTopic(this.Yl);
        matchingRoom.setMaxMemberSize(this.abS);
        matchingRoom.setMinLevel(this.p);
        matchingRoom.setMaxLevel(this.s);
        matchingRoom.broadCast(matchingRoom.infoRoomPacket());
        player.sendPacket((IStaticPacket)SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
    }
}

