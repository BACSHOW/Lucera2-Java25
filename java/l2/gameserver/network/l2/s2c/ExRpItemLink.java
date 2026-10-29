/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExRpItemLink
extends L2GameServerPacket {
    private ItemInfo afH;

    public ExRpItemLink(ItemInfo itemInfo) {
        this.afH = itemInfo;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(108);
        this.writeItemInfo(this.afH);
    }
}

