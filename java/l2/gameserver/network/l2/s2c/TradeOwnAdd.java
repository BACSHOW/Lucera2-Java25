/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class TradeOwnAdd
extends L2GameServerPacket {
    private ItemInfo afH;
    private long aav;

    public TradeOwnAdd(ItemInfo itemInfo, long l) {
        this.afH = itemInfo;
        this.aav = l;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(32);
        this.writeH(1);
        this.writeH(0);
        this.writeD(this.afH.getObjectId());
        this.writeD(this.afH.getItemId());
        this.writeD((int)this.aav);
        this.writeH(this.afH.getItem().getType2ForPackets());
        this.writeH(this.afH.getCustomType1());
        this.writeD(this.afH.getItem().getBodyPart());
        this.writeH(this.afH.getEnchantLevel());
        this.writeH(0);
        this.writeH(0);
    }
}

