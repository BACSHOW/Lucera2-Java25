/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.tables;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class GmListTable {
    public static List<Player> getAllGMs() {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (!player.isGM()) continue;
            arrayList.add(player);
        }
        return arrayList;
    }

    public static List<Player> getAllVisibleGMs() {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (!player.isGM() || player.isInvisible()) continue;
            arrayList.add(player);
        }
        return arrayList;
    }

    public static void sendListToPlayer(Player player) {
        List<Player> list;
        List<Player> list2 = list = Config.HIDE_GM_STATUS ? GmListTable.getAllVisibleGMs() : GmListTable.getAllGMs();
        if (list.isEmpty()) {
            player.sendPacket((IStaticPacket)Msg.THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY);
            return;
        }
        player.sendPacket((IStaticPacket)Msg._GM_LIST_);
        for (Player player2 : list) {
            player.sendPacket((IStaticPacket)new SystemMessage(704).addString(player2.getName()));
        }
    }

    public static void broadcastToGMs(L2GameServerPacket l2GameServerPacket) {
        for (Player player : GmListTable.getAllGMs()) {
            player.sendPacket((IStaticPacket)l2GameServerPacket);
        }
    }

    public static void broadcastMessageToGMs(String string) {
        for (Player player : GmListTable.getAllGMs()) {
            player.sendMessage(string);
        }
    }
}

