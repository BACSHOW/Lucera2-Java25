/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.List;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class BuyList
extends L2GameServerPacket {
    private final int wr;
    private final List<TradeItem> HP;
    private final long adQ;
    private final double adR;

    public BuyList(BuyListHolder.NpcTradeList npcTradeList, Player player, double d) {
        this.adQ = player.getAdena();
        this.adR = d;
        if (npcTradeList != null) {
            this.wr = npcTradeList.getListId();
            this.HP = npcTradeList.getItems();
            player.setBuyListId(this.wr);
        } else {
            this.wr = 0;
            this.HP = Collections.emptyList();
            player.setBuyListId(0);
        }
    }

    @Override
    protected void writeImpl() {
        this.writeC(17);
        this.writeD((int)this.adQ);
        this.writeD(this.wr);
        this.writeH(this.HP.size());
        for (TradeItem tradeItem : this.HP) {
            this.writeH(tradeItem.getType1());
            this.writeD(tradeItem.getObjectId());
            this.writeD(tradeItem.getItemId());
            this.writeD((int)tradeItem.getCurrentValue());
            this.writeH(tradeItem.getType2());
            this.writeH(tradeItem.getCustomType1());
            this.writeD(tradeItem.getBodyPart());
            this.writeH(tradeItem.getEnchantLevel());
            this.writeD(0);
            this.writeD((int)((double)tradeItem.getOwnersPrice() * (1.0 + this.adR)));
        }
    }
}

