/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExMpccRoomMember
extends L2GameServerPacket {
    private int _type;
    private List<MpccRoomMemberInfo> Go = Collections.emptyList();

    public ExMpccRoomMember(MatchingRoom matchingRoom, Player player) {
        this._type = matchingRoom.getMemberType(player);
        this.Go = new ArrayList<MpccRoomMemberInfo>(matchingRoom.getPlayers().size());
        for (Player player2 : matchingRoom.getPlayers()) {
            this.Go.add(new MpccRoomMemberInfo(player2, matchingRoom.getMemberType(player2)));
        }
    }

    @Override
    public void writeImpl() {
        this.writeEx(14);
        this.writeD(this._type);
        this.writeD(this.Go.size());
        for (MpccRoomMemberInfo mpccRoomMemberInfo : this.Go) {
            this.writeD(mpccRoomMemberInfo.objectId);
            this.writeS(mpccRoomMemberInfo.name);
            this.writeD(mpccRoomMemberInfo.level);
            this.writeD(mpccRoomMemberInfo.classId);
            this.writeD(mpccRoomMemberInfo.location);
            this.writeD(mpccRoomMemberInfo.memberType);
        }
    }

    static class MpccRoomMemberInfo {
        public final int objectId;
        public final int classId;
        public final int level;
        public final int location;
        public final int memberType;
        public final String name;

        public MpccRoomMemberInfo(Player player, int n) {
            this.objectId = player.getObjectId();
            this.name = player.getName();
            this.classId = player.getClassId().ordinal();
            this.level = player.getLevel();
            this.location = MatchingRoomManager.getInstance().getLocation(player);
            this.memberType = n;
        }
    }
}

