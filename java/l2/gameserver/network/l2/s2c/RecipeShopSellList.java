/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RecipeShopSellList
extends L2GameServerPacket {
    private int alT;
    private int curMp;
    private int maxMp;
    private long alQ;
    private List<ManufactureItem> alN;

    public RecipeShopSellList(Player player, Player player2) {
        this.alT = player2.getObjectId();
        this.curMp = (int)player2.getCurrentMp();
        this.maxMp = player2.getMaxMp();
        this.alQ = player.getAdena();
        this.alN = player2.getCreateList();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(217);
        this.writeD(this.alT);
        this.writeD(this.curMp);
        this.writeD(this.maxMp);
        this.writeD((int)this.alQ);
        this.writeD(this.alN.size());
        for (ManufactureItem manufactureItem : this.alN) {
            this.writeD(manufactureItem.getRecipeId());
            this.writeD(0);
            this.writeD((int)manufactureItem.getCost());
        }
    }
}

