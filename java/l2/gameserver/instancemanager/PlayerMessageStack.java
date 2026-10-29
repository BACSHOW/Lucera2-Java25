/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PlayerMessageStack {
    private static PlayerMessageStack AR;
    private final Map<Integer, List<L2GameServerPacket>> AS = new HashMap<Integer, List<L2GameServerPacket>>();

    public static PlayerMessageStack getInstance() {
        if (AR == null) {
            AR = new PlayerMessageStack();
        }
        return AR;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void mailto(int n, L2GameServerPacket l2GameServerPacket) {
        Player player = GameObjectsStorage.getPlayer(n);
        if (player != null) {
            player.sendPacket((IStaticPacket)l2GameServerPacket);
            return;
        }
        Map<Integer, List<L2GameServerPacket>> map = this.AS;
        synchronized (map) {
            List<L2GameServerPacket> list = this.AS.containsKey(n) ? this.AS.remove(n) : new ArrayList<L2GameServerPacket>();
            list.add(l2GameServerPacket);
            this.AS.put(n, list);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void CheckMessages(Player player) {
        List<L2GameServerPacket> list = null;
        Map<Integer, List<L2GameServerPacket>> map = this.AS;
        synchronized (map) {
            if (!this.AS.containsKey(player.getObjectId())) {
                return;
            }
            list = this.AS.remove(player.getObjectId());
        }
        if (list == null || list.size() == 0) {
            return;
        }
        for (L2GameServerPacket l2GameServerPacket : list) {
            player.sendPacket((IStaticPacket)l2GameServerPacket);
        }
    }
}

