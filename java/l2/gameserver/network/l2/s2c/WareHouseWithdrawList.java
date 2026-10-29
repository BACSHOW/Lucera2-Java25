/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.item.ItemTemplate;

public class WareHouseWithdrawList
extends L2GameServerPacket {
    private long adQ;
    private List<ItemInfo> akq = new ArrayList<ItemInfo>();
    private int _type;

    public WareHouseWithdrawList(Player player, Warehouse.WarehouseType warehouseType, ItemTemplate.ItemClass itemClass) {
        ItemInstance[] itemInstanceArray;
        this.adQ = player.getAdena();
        this._type = warehouseType.ordinal();
        switch (warehouseType) {
            case PRIVATE: {
                itemInstanceArray = player.getWarehouse().getItems(itemClass);
                break;
            }
            case FREIGHT: {
                itemInstanceArray = player.getFreight().getItems(itemClass);
                break;
            }
            case CLAN: 
            case CASTLE: {
                itemInstanceArray = player.getClan().getWarehouse().getItems(itemClass);
                break;
            }
            default: {
                this.akq = Collections.emptyList();
                return;
            }
        }
        this.akq = new ArrayList<ItemInfo>(itemInstanceArray.length);
        ArrayUtils.eqSort(itemInstanceArray, Warehouse.ItemClassComparator.getInstance());
        for (ItemInstance itemInstance : itemInstanceArray) {
            this.akq.add(new ItemInfo(itemInstance));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(66);
        this.writeH(this._type);
        this.writeD((int)this.adQ);
        this.writeH(this.akq.size());
        for (ItemInfo itemInfo : this.akq) {
            this.writeH(itemInfo.getItem().getType1());
            this.writeD(itemInfo.getObjectId());
            this.writeD(itemInfo.getItemId());
            this.writeD((int)itemInfo.getCount());
            this.writeH(itemInfo.getItem().getType2ForPackets());
            this.writeH(itemInfo.getCustomType1());
            this.writeD(itemInfo.getItem().getBodyPart());
            this.writeH(itemInfo.getEnchantLevel());
            this.writeH(itemInfo.getCustomType2());
            this.writeH(0);
            this.writeD(itemInfo.getObjectId());
            this.writeD(itemInfo.getVariationStat1());
            this.writeD(itemInfo.getVariationStat2());
        }
    }
}

