/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public final class BuyListSeed
extends L2GameServerPacket {
    private int Gh;
    private List<TradeItem> adS = new ArrayList<TradeItem>();
    private long adT;

    public BuyListSeed(BuyListHolder.NpcTradeList npcTradeList, int n, long l) {
        this.adT = l;
        this.Gh = n;
        this.adS = npcTradeList.getItems();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(232);
        this.writeD((int)this.adT);
        this.writeD(this.Gh);
        this.writeH(this.adS.size());
        for (TradeItem tradeItem : this.adS) {
            this.writeH(tradeItem.getItem().getType1());
            this.writeD(tradeItem.getObjectId());
            this.writeD(tradeItem.getItemId());
            this.writeD((int)tradeItem.getCount());
            this.writeH(tradeItem.getItem().getType2ForPackets());
            this.writeH(tradeItem.getCustomType1());
            this.writeD((int)tradeItem.getOwnersPrice());
        }
    }
}

