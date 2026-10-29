/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RecipeBookItemList;

public class RequestRecipeItemDelete
extends L2GameClientPacket {
    private int XE;

    @Override
    protected void readImpl() {
        this.XE = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getPrivateStoreType() == 5) {
            player.sendActionFailed();
            return;
        }
        Recipe recipe = RecipeHolder.getInstance().getRecipeById(this.XE);
        if (recipe == null) {
            player.sendActionFailed();
            return;
        }
        player.unregisterRecipe(this.XE);
        player.sendPacket((IStaticPacket)new RecipeBookItemList(player, recipe.getType() == Recipe.ERecipeType.ERT_DWARF));
    }
}

