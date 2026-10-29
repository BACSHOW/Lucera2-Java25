/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExStorageMaxCount
extends L2GameServerPacket {
    private int NI;
    private int NJ;
    private int NK;
    private int aip;
    private int aiq;
    private int NM;
    private int NN;

    public ExStorageMaxCount(Player player) {
        this.NI = player.getInventoryLimit();
        this.NJ = player.getWarehouseLimit();
        this.NK = Config.WAREHOUSE_SLOTS_CLAN;
        this.aiq = this.aip = player.getTradeLimit();
        this.NM = player.getDwarvenRecipeLimit();
        this.NN = player.getCommonRecipeLimit();
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(46);
        this.writeD(this.NI);
        this.writeD(this.NJ);
        this.writeD(this.NK);
        this.writeD(this.aip);
        this.writeD(this.aiq);
        this.writeD(this.NM);
        this.writeD(this.NN);
    }
}

