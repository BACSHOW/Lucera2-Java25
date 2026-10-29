/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RecipeShopItemInfo
extends L2GameServerPacket {
    private int XE;
    private int alK;
    private int alL;
    private int _maxMp;
    private int alM = -1;
    private long _price;

    public RecipeShopItemInfo(Player player, Player player2, int n, long l, int n2) {
        this.XE = n;
        this.alK = player2.getObjectId();
        this._price = l;
        this.alM = n2;
        this.alL = (int)player2.getCurrentMp();
        this._maxMp = player2.getMaxMp();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(218);
        this.writeD(this.alK);
        this.writeD(this.XE);
        this.writeD(this.alL);
        this.writeD(this._maxMp);
        this.writeD(this.alM);
        this.writeD((int)this._price);
    }
}

