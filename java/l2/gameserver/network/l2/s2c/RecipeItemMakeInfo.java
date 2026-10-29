/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RecipeItemMakeInfo
extends L2GameServerPacket {
    private int _id;
    private int alH;
    private int _status;
    private int alI;
    private int alJ;

    public RecipeItemMakeInfo(Player player, Recipe recipe, int n) {
        this._id = recipe.getId();
        this.alH = recipe.getType().ordinal();
        this._status = n;
        this.alI = (int)player.getCurrentMp();
        this.alJ = player.getMaxMp();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(215);
        this.writeD(this._id);
        this.writeD(this.alH);
        this.writeD(this.alI);
        this.writeD(this.alJ);
        this.writeD(this._status);
    }
}

