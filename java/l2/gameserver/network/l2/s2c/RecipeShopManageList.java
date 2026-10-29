/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RecipeShopManageList
extends L2GameServerPacket {
    private List<ManufactureItem> alN;
    private Collection<Recipe> alO;
    private int alP;
    private long alQ;
    private boolean alR;

    public RecipeShopManageList(Player player, boolean bl) {
        this.alP = player.getObjectId();
        this.alQ = player.getAdena();
        this.alR = bl;
        this.alO = this.alR ? player.getDwarvenRecipeBook() : player.getCommonRecipeBook();
        this.alN = player.getCreateList();
        for (ManufactureItem manufactureItem : this.alN) {
            if (player.findRecipe(manufactureItem.getRecipeId())) continue;
            this.alN.remove(manufactureItem);
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(216);
        this.writeD(this.alP);
        this.writeD((int)Math.min(this.alQ, Integer.MAX_VALUE));
        this.writeD(this.alR ? 0 : 1);
        this.writeD(this.alO.size());
        int n = 1;
        for (Recipe object : this.alO) {
            this.writeD(object.getId());
            this.writeD(n++);
        }
        this.writeD(this.alN.size());
        for (ManufactureItem manufactureItem : this.alN) {
            this.writeD(manufactureItem.getRecipeId());
            this.writeD(0);
            this.writeD((int)manufactureItem.getCost());
        }
    }
}

