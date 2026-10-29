/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.Map;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.actor.instances.player.Friend;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestFriendList
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
        player.sendPacket((IStaticPacket)Msg._FRIENDS_LIST_);
        Map<Integer, Friend> map = player.getFriendList().getList();
        for (Map.Entry<Integer, Friend> entry : map.entrySet()) {
            Player player2 = World.getPlayer(entry.getKey());
            if (player2 != null) {
                player.sendPacket((IStaticPacket)new SystemMessage(488).addName(player2));
                continue;
            }
            player.sendPacket((IStaticPacket)new SystemMessage(489).addString(entry.getValue().getName()));
        }
        player.sendPacket((IStaticPacket)Msg.__EQUALS__);
    }
}

