/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExManageMpccRoomMember
extends L2GameServerPacket {
    public static int ADD_MEMBER = 0;
    public static int UPDATE_MEMBER = 1;
    public static int REMOVE_MEMBER = 2;
    private int _type;
    private MpccRoomMemberInfo agX;

    public ExManageMpccRoomMember(int n, MatchingRoom matchingRoom, Player player) {
        this._type = n;
        this.agX = new MpccRoomMemberInfo(player, matchingRoom.getMemberType(player));
    }

    @Override
    protected void writeImpl() {
        this.writeEx(16);
        this.writeD(this._type);
        this.writeD(this.agX.objectId);
        this.writeS(this.agX.name);
        this.writeD(this.agX.classId);
        this.writeD(this.agX.level);
        this.writeD(this.agX.location);
        this.writeD(this.agX.memberType);
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

