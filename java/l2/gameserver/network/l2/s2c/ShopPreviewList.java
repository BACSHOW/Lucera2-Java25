/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.item.ItemTemplate;

public class ShopPreviewList
extends L2GameServerPacket {
    private int wr;
    private List<ItemInfo> akq;
    private long adT;

    public ShopPreviewList(BuyListHolder.NpcTradeList npcTradeList, Player player) {
        this.wr = npcTradeList.getListId();
        this.adT = player.getAdena();
        List<TradeItem> list = npcTradeList.getItems();
        this.akq = new ArrayList<ItemInfo>(list.size());
        for (TradeItem tradeItem : npcTradeList.getItems()) {
            if (!tradeItem.getItem().isEquipable()) continue;
            this.akq.add(tradeItem);
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(239);
        this.writeC(192);
        this.writeC(19);
        this.writeC(0);
        this.writeC(0);
        this.writeD((int)this.adT);
        this.writeD(this.wr);
        this.writeH(this.akq.size());
        for (ItemInfo itemInfo : this.akq) {
            if (!itemInfo.getItem().isEquipable()) continue;
            this.writeD(itemInfo.getItemId());
            this.writeH(itemInfo.getItem().getType2ForPackets());
            this.writeH(itemInfo.getItem().isEquipable() ? itemInfo.getItem().getBodyPart() : 0);
            this.writeD(ShopPreviewList.getWearPrice(itemInfo.getItem()));
        }
    }

    public static int getWearPrice(ItemTemplate itemTemplate) {
        switch (itemTemplate.getItemGrade()) {
            case D: {
                return Config.WEAING_D_GRADE_PRICE;
            }
            case C: {
                return Config.WEAING_C_GRADE_PRICE;
            }
            case B: {
                return Config.WEAING_B_GRADE_PRICE;
            }
            case A: {
                return Config.WEAING_A_GRADE_PRICE;
            }
            case S: {
                return Config.WEAING_S_GRADE_PRICE;
            }
        }
        return Config.WEAING_NON_GRADE_PRICE;
    }
}

