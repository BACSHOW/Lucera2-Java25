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

public class L2FriendList
extends L2GameServerPacket {
    private List<FriendInfo> adS = Collections.emptyList();

    public L2FriendList(Player player) {
        Map<Integer, Friend> map = player.getFriendList().getList();
        this.adS = new ArrayList<FriendInfo>(map.size());
        for (Map.Entry<Integer, Friend> entry : map.entrySet()) {
            FriendInfo friendInfo = new FriendInfo();
            friendInfo.CS = entry.getKey();
            friendInfo._name = entry.getValue().getName();
            friendInfo.pb = entry.getValue().isOnline();
            this.adS.add(friendInfo);
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(250);
        this.writeD(this.adS.size());
        for (FriendInfo friendInfo : this.adS) {
            this.writeD(0);
            this.writeS(friendInfo._name);
            this.writeD(friendInfo.pb ? 1 : 0);
            this.writeD(friendInfo.CS);
        }
    }

    private static class FriendInfo {
        private int CS;
        private String _name;
        private boolean pb;

        private FriendInfo() {
        }
    }
}

