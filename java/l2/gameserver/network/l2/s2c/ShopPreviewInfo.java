/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Map;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ShopPreviewInfo
extends L2GameServerPacket {
    private Map<Integer, Integer> amm;

    public ShopPreviewInfo(Map<Integer, Integer> map) {
        this.amm = map;
    }

    @Override
    protected void writeImpl() {
        this.writeC(240);
        this.writeD(17);
        this.writeD(this.V(0));
        this.writeD(this.V(1));
        this.writeD(this.V(2));
        this.writeD(this.V(3));
        this.writeD(this.V(4));
        this.writeD(this.V(5));
        this.writeD(this.V(7));
        this.writeD(this.V(8));
        this.writeD(this.V(9));
        this.writeD(this.V(10));
        this.writeD(this.V(11));
        this.writeD(this.V(12));
        this.writeD(this.V(15));
        this.writeD(this.V(14));
        this.writeD(this.V(15));
        this.writeD(this.V(16));
    }

    private int V(int n) {
        return this.amm.get(n) != null ? this.amm.get(n) : 0;
    }
}

