/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PrivateStoreManageListBuy
extends L2GameServerPacket {
    private int acx;
    private long adQ;
    private List<TradeItem> alA;
    private List<TradeItem> HP;

    public PrivateStoreManageListBuy(Player player) {
        this.acx = player.getObjectId();
        this.adQ = player.getAdena();
        this.alA = player.getBuyList();
        this.HP = new ArrayList<TradeItem>();
        LinkedList<ItemInstance> linkedList = new LinkedList<ItemInstance>(Arrays.asList(player.getInventory().getItems()));
        Collections.sort(linkedList, Warehouse.ItemClassComparator.getInstance());
        for (ItemInstance itemInstance : linkedList) {
            if (!itemInstance.canBeTraded(player) || itemInstance.getItemId() == 57) continue;
            TradeItem tradeItem = new TradeItem(itemInstance);
            this.HP.add(tradeItem);
            tradeItem.setObjectId(0);
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(183);
        this.writeD(this.acx);
        this.writeD((int)this.adQ);
        this.writeD(this.HP.size());
        for (TradeItem tradeItem : this.HP) {
            this.writeD(tradeItem.getItemId());
            this.writeH(tradeItem.getEnchantLevel());
            this.writeD((int)tradeItem.getCount());
            this.writeD((int)tradeItem.getStorePrice());
            this.writeH(tradeItem.getCustomType2());
            this.writeD(tradeItem.getBodyPart());
            this.writeH(tradeItem.getType2());
        }
        this.writeD(this.alA.size());
        for (TradeItem tradeItem : this.alA) {
            this.writeD(tradeItem.getItemId());
            this.writeH(tradeItem.getEnchantLevel());
            this.writeD((int)tradeItem.getCount());
            this.writeD((int)tradeItem.getStorePrice());
            this.writeH(tradeItem.getCustomType2());
            this.writeD(tradeItem.getBodyPart());
            this.writeH(tradeItem.getType2());
            this.writeD((int)tradeItem.getOwnersPrice());
            this.writeD((int)tradeItem.getReferencePrice());
        }
    }
}

