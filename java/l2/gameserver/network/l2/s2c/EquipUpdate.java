/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

@Deprecated
public class EquipUpdate
extends L2GameServerPacket {
    private ItemInfo afH;

    public EquipUpdate(ItemInstance itemInstance, int n) {
        this.afH = new ItemInfo(itemInstance);
        this.afH.setLastChange(n);
    }

    @Override
    protected final void writeImpl() {
        this.writeC(75);
        this.writeD(this.afH.getLastChange());
        this.writeD(this.afH.getObjectId());
        this.writeD(this.afH.getEquipSlot());
    }
}

