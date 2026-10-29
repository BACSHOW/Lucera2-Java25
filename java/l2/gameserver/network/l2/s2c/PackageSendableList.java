/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.LinkedList;
import java.util.List;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.network.l2.c2s.RequestPackageSend;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PackageSendableList
extends L2GameServerPacket {
    private int acX;
    private long adQ;
    private List<ItemInfo> akq;

    public PackageSendableList(int n, Player player) {
        this.adQ = player.getAdena();
        this.acX = n;
        ItemInstance[] itemInstanceArray = player.getInventory().getItems();
        ArrayUtils.eqSort(itemInstanceArray, Warehouse.ItemClassComparator.getInstance());
        this.akq = new LinkedList<ItemInfo>();
        for (ItemInstance itemInstance : itemInstanceArray) {
            if (!RequestPackageSend.CanSendItem(itemInstance)) continue;
            this.akq.add(new ItemInfo(itemInstance));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(195);
        this.writeD(this.acX);
        this.writeD((int)this.adQ);
        this.writeD(this.akq.size());
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
        }
    }
}

