/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class TradeOtherAdd
extends L2GameServerPacket {
    private ItemInfo anr;
    private long aav;

    public TradeOtherAdd(ItemInfo itemInfo, long l) {
        this.anr = itemInfo;
        this.aav = l;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(33);
        this.writeH(1);
        this.writeH(0);
        this.writeD(this.anr.getObjectId());
        this.writeD(this.anr.getItemId());
        this.writeD((int)this.aav);
        this.writeH(this.anr.getItem().getType2ForPackets());
        this.writeH(this.anr.getCustomType1());
        this.writeD(this.anr.getItem().getBodyPart());
        this.writeH(this.anr.getEnchantLevel());
        this.writeH(0);
        this.writeH(0);
    }
}

