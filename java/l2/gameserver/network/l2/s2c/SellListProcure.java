/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.manor.CropProcure;

public class SellListProcure
extends L2GameServerPacket {
    private long adT;
    private Map<ItemInstance, Long> ama = new HashMap<ItemInstance, Long>();
    private List<CropProcure> acA = new ArrayList<CropProcure>();
    private int amb;

    public SellListProcure(Player player, int n) {
        this.adT = player.getAdena();
        this.amb = n;
        this.acA = ResidenceHolder.getInstance().getResidence(Castle.class, this.amb).getCropProcure(0);
        for (CropProcure cropProcure : this.acA) {
            ItemInstance itemInstance = player.getInventory().getItemByItemId(cropProcure.getId());
            if (itemInstance == null || cropProcure.getAmount() <= 0L) continue;
            this.ama.put(itemInstance, cropProcure.getAmount());
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(233);
        this.writeD((int)this.adT);
        this.writeD(0);
        this.writeH(this.ama.size());
        for (ItemInstance itemInstance : this.ama.keySet()) {
            this.writeH(0);
            this.writeD(itemInstance.getObjectId());
            this.writeD(itemInstance.getItemId());
            this.writeD(this.ama.get(itemInstance).intValue());
            this.writeH(itemInstance.getTemplate().getType2ForPackets());
            this.writeH(0);
            this.writeD(0);
        }
    }
}

