/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestExJoinMpccRoom
extends L2GameClientPacket {
    private int abG;

    @Override
    protected void readImpl() throws Exception {
        this.abG = this.readD();
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getMatchingRoom() != null) {
            return;
        }
        MatchingRoom matchingRoom = MatchingRoomManager.getInstance().getMatchingRoom(MatchingRoom.CC_MATCHING, this.abG);
        if (matchingRoom == null) {
            return;
        }
        matchingRoom.addMember(player);
    }
}

