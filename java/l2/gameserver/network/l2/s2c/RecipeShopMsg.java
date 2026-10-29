/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RecipeShopMsg
extends L2GameServerPacket {
    private int CS;
    private String alS;

    public RecipeShopMsg(Player player) {
        this.CS = player.getObjectId();
        this.alS = player.getManufactureName();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(219);
        this.writeD(this.CS);
        this.writeS(this.alS);
    }
}

