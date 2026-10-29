/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PrivateStoreListBuy
extends L2GameServerPacket {
    private int acx;
    private long adQ;
    private List<TradeItem> HM;

    public PrivateStoreListBuy(Player player, Player player2) {
        this.adQ = player.getAdena();
        this.acx = player2.getObjectId();
        this.HM = new ArrayList<TradeItem>();
        List<TradeItem> list = player2.getBuyList();
        LinkedList<ItemInstance> linkedList = new LinkedList<ItemInstance>(Arrays.asList(player.getInventory().getItems()));
        block0: for (TradeItem tradeItem : list) {
            TradeItem tradeItem2 = null;
            Iterator iterator = linkedList.iterator();
            while (iterator.hasNext()) {
                ItemInstance itemInstance = (ItemInstance)iterator.next();
                if (itemInstance.getItemId() != tradeItem.getItemId() || !itemInstance.canBeTraded(player) || Config.PRIVATE_BUY_MATCH_ENCHANT && itemInstance.getEnchantLevel() != tradeItem.getEnchantLevel() || !Config.PRIVATE_BUY_MATCH_ENCHANT && itemInstance.getEnchantLevel() < tradeItem.getEnchantLevel()) continue;
                tradeItem2 = new TradeItem(itemInstance);
                this.HM.add(tradeItem2);
                tradeItem2.setOwnersPrice(tradeItem.getOwnersPrice());
                tradeItem2.setCount(tradeItem.getCount());
                tradeItem2.setCurrentValue(Math.min(tradeItem.getCount(), itemInstance.getCount()));
                tradeItem2.setEnchantLevel(itemInstance.getEnchantLevel());
                iterator.remove();
                continue block0;
            }
            if (tradeItem2 != null) continue;
            tradeItem2 = new TradeItem();
            tradeItem2.setItemId(tradeItem.getItemId());
            tradeItem2.setOwnersPrice(tradeItem.getOwnersPrice());
            tradeItem2.setCount(tradeItem.getCount());
            tradeItem2.setCurrentValue(0L);
            tradeItem2.setEnchantLevel(tradeItem.getEnchantLevel());
            this.HM.add(tradeItem2);
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(184);
        this.writeD(this.acx);
        this.writeD((int)this.adQ);
        this.writeD(this.HM.size());
        for (TradeItem tradeItem : this.HM) {
            this.writeD(tradeItem.getObjectId());
            this.writeD(tradeItem.getItemId());
            this.writeH(tradeItem.getEnchantLevel());
            this.writeD((int)tradeItem.getCurrentValue());
            this.writeD((int)tradeItem.getStorePrice());
            this.writeH(tradeItem.getCustomType2());
            this.writeD(tradeItem.getBodyPart());
            this.writeH(tradeItem.getType2());
            this.writeD((int)tradeItem.getOwnersPrice());
            this.writeD((int)tradeItem.getCount());
        }
    }
}

