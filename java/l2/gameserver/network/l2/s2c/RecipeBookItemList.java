/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Collection;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RecipeBookItemList
extends L2GameServerPacket {
    private Collection<Recipe> alE;
    private final boolean alF;
    private final int alG;

    public RecipeBookItemList(Player player, boolean bl) {
        this.alF = bl;
        this.alG = (int)player.getCurrentMp();
        this.alE = bl ? player.getDwarvenRecipeBook() : player.getCommonRecipeBook();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(214);
        this.writeD(this.alF ? 0 : 1);
        this.writeD(this.alG);
        this.writeD(this.alE.size());
        for (Recipe recipe : this.alE) {
            this.writeD(recipe.getId());
            this.writeD(1);
        }
    }
}

