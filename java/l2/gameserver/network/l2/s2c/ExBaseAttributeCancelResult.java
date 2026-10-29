/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExBaseAttributeCancelResult
extends L2GameServerPacket {
    private boolean agn;
    private int CS;
    private Element _element;

    public ExBaseAttributeCancelResult(boolean bl, ItemInstance itemInstance, Element element) {
        this.agn = bl;
        this.CS = itemInstance.getObjectId();
        this._element = element;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(117);
        this.writeD(this.agn);
        this.writeD(this.CS);
        this.writeD(this._element.getId());
    }
}

