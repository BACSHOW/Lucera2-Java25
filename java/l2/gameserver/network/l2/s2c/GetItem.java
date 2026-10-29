/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class GetItem
extends L2GameServerPacket {
    private int afo;
    private int _itemObjId;
    private Location cH;

    public GetItem(ItemInstance itemInstance, int n) {
        this._itemObjId = itemInstance.getObjectId();
        this.cH = itemInstance.getLoc();
        this.afo = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(13);
        this.writeD(this.afo);
        this.writeD(this._itemObjId);
        this.writeD(this.cH.x);
        this.writeD(this.cH.y);
        this.writeD(this.cH.z);
    }
}

