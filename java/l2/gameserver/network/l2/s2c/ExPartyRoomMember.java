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
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPartyRoomMember
extends L2GameServerPacket {
    private int _type;
    private List<PartyRoomMemberInfo> Go = Collections.emptyList();

    public ExPartyRoomMember(MatchingRoom matchingRoom, Player player) {
        this._type = matchingRoom.getMemberType(player);
        this.Go = new ArrayList<PartyRoomMemberInfo>(matchingRoom.getPlayers().size());
        for (Player player2 : matchingRoom.getPlayers()) {
            this.Go.add(new PartyRoomMemberInfo(player2, matchingRoom.getMemberType(player2)));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(14);
        this.writeD(this._type);
        this.writeD(this.Go.size());
        for (PartyRoomMemberInfo partyRoomMemberInfo : this.Go) {
            this.writeD(partyRoomMemberInfo.objectId);
            this.writeS(partyRoomMemberInfo.name);
            this.writeD(partyRoomMemberInfo.classId);
            this.writeD(partyRoomMemberInfo.level);
            this.writeD(partyRoomMemberInfo.location);
            this.writeD(partyRoomMemberInfo.memberType);
        }
    }

    static class PartyRoomMemberInfo {
        public final int objectId;
        public final int classId;
        public final int level;
        public final int location;
        public final int memberType;
        public final String name;
        public final int[] instanceReuses;

        public PartyRoomMemberInfo(Player player, int n) {
            this.objectId = player.getObjectId();
            this.name = player.getName();
            this.classId = player.getClassId().ordinal();
            this.level = player.getLevel();
            this.location = MatchingRoomManager.getInstance().getLocation(player);
            this.memberType = n;
            this.instanceReuses = ArrayUtils.toArray(player.getInstanceReuses().keySet());
        }
    }
}

