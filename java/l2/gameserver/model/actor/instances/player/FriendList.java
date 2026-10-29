/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.instances.player;

import java.util.Collections;
import java.util.Map;
import l2.gameserver.dao.CharacterFriendDAO;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.actor.instances.player.Friend;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2Friend;
import l2.gameserver.network.l2.s2c.L2FriendStatus;
import l2.gameserver.network.l2.s2c.SystemMessage;
import org.apache.commons.lang3.StringUtils;

public class FriendList {
    private Map<Integer, Friend> Nq = Collections.emptyMap();
    private final Player LF;

    public FriendList(Player player) {
        this.LF = player;
    }

    public void restore() {
        this.Nq = CharacterFriendDAO.getInstance().select(this.LF);
    }

    public void removeFriend(String string) {
        if (StringUtils.isEmpty(string)) {
            return;
        }
        int n = this.s(string);
        if (n > 0) {
            Player player = World.getPlayer(n);
            this.LF.sendPacket(new SystemMessage(133).addString(string), new L2Friend(string, false, player != null, n));
            if (player != null) {
                player.sendPacket(new SystemMessage(481).addString(this.LF.getName()), new L2Friend(this.LF, false));
            }
        } else {
            this.LF.sendPacket((IStaticPacket)new SystemMessage(171).addString(string));
        }
    }

    public void notifyFriends(boolean bl) {
        try {
            for (Friend friend : this.Nq.values()) {
                Friend friend2;
                Player player = GameObjectsStorage.getPlayer(friend.getObjectId());
                if (player == null || (friend2 = player.getFriendList().getList().get(this.LF.getObjectId())) == null) continue;
                friend2.update(this.LF, bl);
                if (bl) {
                    player.sendPacket((IStaticPacket)new SystemMessage(503).addString(this.LF.getName()));
                }
                player.sendPacket((IStaticPacket)new L2FriendStatus(this.LF, bl));
                friend.update(player, bl);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void addFriend(Player player) {
        this.Nq.put(player.getObjectId(), new Friend(player));
        CharacterFriendDAO.getInstance().insert(this.LF, player);
    }

    private int s(String string) {
        if (string == null) {
            return 0;
        }
        Integer n = 0;
        for (Map.Entry<Integer, Friend> entry : this.Nq.entrySet()) {
            if (!string.equalsIgnoreCase(entry.getValue().getName())) continue;
            n = entry.getKey();
            break;
        }
        if (n > 0) {
            this.Nq.remove(n);
            CharacterFriendDAO.getInstance().delete(this.LF, n);
            return n;
        }
        return 0;
    }

    public Map<Integer, Friend> getList() {
        return this.Nq;
    }

    public String toString() {
        return "FriendList[owner=" + this.LF.getName() + "]";
    }
}

