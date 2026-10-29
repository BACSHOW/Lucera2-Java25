/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestWithdrawPartyRoom
extends L2GameClientPacket {
    private int abG;

    @Override
    protected void readImpl() {
        this.abG = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        MatchingRoom matchingRoom = player.getMatchingRoom();
        if (matchingRoom.getId() != this.abG || matchingRoom.getType() != MatchingRoom.PARTY_MATCHING) {
            return;
        }
        if (matchingRoom.getLeader() == player) {
            return;
        }
        matchingRoom.removeMember(player, false);
    }
}

