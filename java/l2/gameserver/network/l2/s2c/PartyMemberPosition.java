/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.HashMap;
import java.util.Map;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class PartyMemberPosition
extends L2GameServerPacket {
    private final Map<Integer, Location> aks = new HashMap<Integer, Location>();

    public PartyMemberPosition add(Player player) {
        this.aks.put(player.getObjectId(), player.getLoc());
        return this;
    }

    public int size() {
        return this.aks.size();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(167);
        this.writeD(this.aks.size());
        for (Map.Entry<Integer, Location> entry : this.aks.entrySet()) {
            this.writeD(entry.getKey());
            this.writeD(entry.getValue().x);
            this.writeD(entry.getValue().y);
            this.writeD(entry.getValue().z);
        }
    }
}

