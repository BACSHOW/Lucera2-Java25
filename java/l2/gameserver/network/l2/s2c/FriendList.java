/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Friend;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class FriendList
extends L2GameServerPacket {
    private List<FriendInfo> aiB = Collections.emptyList();

    public FriendList(Player player) {
        Map<Integer, Friend> map = player.getFriendList().getList();
        this.aiB = new ArrayList<FriendInfo>(map.size());
        for (Map.Entry<Integer, Friend> entry : map.entrySet()) {
            Friend friend = entry.getValue();
            FriendInfo friendInfo = new FriendInfo();
            friendInfo.name = friend.getName();
            friendInfo.classId = friend.getClassId();
            friendInfo.objectId = entry.getKey();
            friendInfo.level = friend.getLevel();
            friendInfo.aiC = friend.isOnline();
            this.aiB.add(friendInfo);
        }
    }

    @Override
    protected void writeImpl() {
        this.writeC(250);
        this.writeD(this.aiB.size());
        for (FriendInfo friendInfo : this.aiB) {
            this.writeD(friendInfo.objectId);
            this.writeS(friendInfo.name);
            this.writeD(friendInfo.aiC);
            this.writeD(friendInfo.aiC ? friendInfo.objectId : 0);
        }
    }

    private class FriendInfo {
        private String name;
        private int objectId;
        private boolean aiC;
        private int level;
        private int classId;

        private FriendInfo() {
        }
    }
}

