/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.model.matching.PartyMatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestPartyMatchList
extends L2GameClientPacket {
    private int acq;
    private int acr;
    private int p;
    private int s;
    private int abG;
    private String acs;

    @Override
    protected void readImpl() {
        this.abG = this.readD();
        this.acr = this.readD();
        this.p = this.readD();
        this.s = this.readD();
        this.acq = this.readD();
        this.acs = this.readS(64);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        MatchingRoom matchingRoom = player.getMatchingRoom();
        if (matchingRoom == null) {
            new PartyMatchingRoom(player, this.p, this.s, this.acr, this.acq, this.acs);
        } else if (matchingRoom.getId() == this.abG && matchingRoom.getType() == MatchingRoom.PARTY_MATCHING && matchingRoom.getLeader() == player) {
            matchingRoom.setMinLevel(this.p);
            matchingRoom.setMaxLevel(this.s);
            matchingRoom.setMaxMemberSize(this.acr);
            matchingRoom.setTopic(this.acs);
            matchingRoom.setLootType(this.acq);
            matchingRoom.broadCast(matchingRoom.infoRoomPacket());
        }
    }
}

