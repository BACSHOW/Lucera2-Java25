/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.Map;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PackageToList
extends L2GameServerPacket {
    private Map<Integer, String> akr = Collections.emptyMap();

    public PackageToList(Player player) {
        this.akr = player.getAccountChars();
    }

    @Override
    protected void writeImpl() {
        this.writeC(194);
        this.writeD(this.akr.size());
        for (Map.Entry<Integer, String> entry : this.akr.entrySet()) {
            this.writeD(entry.getKey());
            this.writeS(entry.getValue());
        }
    }
}

