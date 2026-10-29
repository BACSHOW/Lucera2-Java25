/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ListPartyWaiting
extends L2GameServerPacket {
    private Collection<MatchingRoom> ajB;
    private int agV;

    public ListPartyWaiting(int n, boolean bl, int n2, Player player) {
        int n3 = (n2 - 1) * 64;
        int n4 = n2 * 64;
        this.ajB = new ArrayList<MatchingRoom>();
        int n5 = 0;
        List<MatchingRoom> list = MatchingRoomManager.getInstance().getMatchingRooms(MatchingRoom.PARTY_MATCHING, n, bl, player);
        this.agV = list.size();
        for (MatchingRoom matchingRoom : list) {
            if (n5 < n3 || n5 >= n4) continue;
            this.ajB.add(matchingRoom);
            ++n5;
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(150);
        this.writeD(this.agV);
        this.writeD(this.ajB.size());
        for (MatchingRoom matchingRoom : this.ajB) {
            this.writeD(matchingRoom.getId());
            this.writeS(matchingRoom.getLeader() == null ? "None" : matchingRoom.getLeader().getName());
            this.writeD(matchingRoom.getLocationId());
            this.writeD(matchingRoom.getMinLevel());
            this.writeD(matchingRoom.getMaxLevel());
            this.writeD(matchingRoom.getMaxMembersSize());
            this.writeS(matchingRoom.getTopic());
            Collection<Player> collection = matchingRoom.getPlayers();
            this.writeD(collection.size());
            for (Player player : collection) {
                this.writeD(player.getClassId().getId());
                this.writeS(player.getName());
            }
        }
    }
}

