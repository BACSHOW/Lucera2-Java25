/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SpawnItem
extends L2GameServerPacket {
    private int CS;
    private int _itemId;
    private int _x;
    private int _y;
    private int Di;
    private int afD;
    private long CE;

    public SpawnItem(ItemInstance itemInstance) {
        this.CS = itemInstance.getObjectId();
        this._itemId = itemInstance.getItemId();
        this._x = itemInstance.getX();
        this._y = itemInstance.getY();
        this.Di = itemInstance.getZ();
        this.afD = itemInstance.isStackable() ? 1 : 0;
        this.CE = itemInstance.getCount();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(11);
        this.writeD(this.CS);
        this.writeD(this._itemId);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di + Config.CLIENT_Z_SHIFT);
        this.writeD(this.afD);
        this.writeD((int)this.CE);
        this.writeD(0);
    }
}

