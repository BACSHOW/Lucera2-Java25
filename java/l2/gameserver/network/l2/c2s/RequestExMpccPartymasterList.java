/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.HashSet;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExMpccPartymasterList;

public class RequestExMpccPartymasterList
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        MatchingRoom matchingRoom = player.getMatchingRoom();
        if (matchingRoom == null || matchingRoom.getType() != MatchingRoom.CC_MATCHING) {
            return;
        }
        HashSet<String> hashSet = new HashSet<String>();
        for (Player player2 : matchingRoom.getPlayers()) {
            if (player2.getParty() == null) continue;
            hashSet.add(player2.getParty().getPartyLeader().getName());
        }
        player.sendPacket((IStaticPacket)new ExMpccPartymasterList(hashSet));
    }
}

