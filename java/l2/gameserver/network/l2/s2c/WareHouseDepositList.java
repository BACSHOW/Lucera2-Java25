/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class WareHouseDepositList
extends L2GameServerPacket {
    private int anE;
    private long adQ;
    private List<ItemInfo> akq;

    public WareHouseDepositList(Player player, Warehouse.WarehouseType warehouseType) {
        this.anE = warehouseType.ordinal();
        this.adQ = player.getAdena();
        ItemInstance[] itemInstanceArray = player.getInventory().getItems();
        ArrayUtils.eqSort(itemInstanceArray, Warehouse.ItemClassComparator.getInstance());
        this.akq = new ArrayList<ItemInfo>(itemInstanceArray.length);
        for (ItemInstance itemInstance : itemInstanceArray) {
            if (!itemInstance.canBeStored(player, this.anE == 1)) continue;
            this.akq.add(new ItemInfo(itemInstance));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(65);
        this.writeH(this.anE);
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

