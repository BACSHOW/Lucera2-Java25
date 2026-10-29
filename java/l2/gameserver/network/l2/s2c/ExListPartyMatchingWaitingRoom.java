/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExListPartyMatchingWaitingRoom
extends L2GameServerPacket {
    private List<PartyMatchingWaitingInfo> agW = Collections.emptyList();
    private final int agV;

    public ExListPartyMatchingWaitingRoom(Player player, int n, int n2, int n3, int[] nArray) {
        int n4 = (n3 - 1) * 64;
        int n5 = n3 * 64;
        int n6 = 0;
        List<Player> list = MatchingRoomManager.getInstance().getWaitingList(n, n2, nArray);
        this.agV = list.size();
        this.agW = new ArrayList<PartyMatchingWaitingInfo>(this.agV);
        for (Player player2 : list) {
            if (n6 < n4 || n6 >= n5) continue;
            this.agW.add(new PartyMatchingWaitingInfo(player2));
            ++n6;
        }
    }

    @Override
    protected void writeImpl() {
        this.writeEx(54);
        this.writeD(this.agV);
        this.writeD(this.agW.size());
        for (PartyMatchingWaitingInfo partyMatchingWaitingInfo : this.agW) {
            this.writeS(partyMatchingWaitingInfo.name);
            this.writeD(partyMatchingWaitingInfo.classId);
            this.writeD(partyMatchingWaitingInfo.level);
        }
    }

    static class PartyMatchingWaitingInfo {
        public final int classId;
        public final int level;
        public final int currentInstance;
        public final String name;
        public final int[] instanceReuses;

        public PartyMatchingWaitingInfo(Player player) {
            this.name = player.getName();
            this.classId = player.getClassId().getId();
            this.level = player.getLevel();
            Reflection reflection = player.getReflection();
            this.currentInstance = reflection == null ? 0 : reflection.getInstancedZoneId();
            this.instanceReuses = ArrayUtils.toArray(player.getInstanceReuses().keySet());
        }
    }
}

