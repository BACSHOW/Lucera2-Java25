/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.RecipeItemMakeInfo;

public class RequestRecipeItemMakeInfo
extends L2GameClientPacket {
    private int _id;

    @Override
    protected void readImpl() {
        this._id = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Recipe recipe = RecipeHolder.getInstance().getRecipeById(this._id);
        if (recipe == null) {
            player.sendActionFailed();
            return;
        }
        this.sendPacket((L2GameServerPacket)new RecipeItemMakeInfo(player, recipe, -1));
    }
}

