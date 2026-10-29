/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class GMViewWarehouseWithdrawList
extends L2GameServerPacket {
    private final ItemInstance[] ahC;
    private String agb;
    private long ajm;

    public GMViewWarehouseWithdrawList(Player player) {
        this.agb = player.getName();
        this.ajm = player.getAdena();
        this.ahC = player.getWarehouse().getItems();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(149);
        this.writeS(this.agb);
        this.writeD((int)this.ajm);
        this.writeH(this.ahC.length);
        for (ItemInstance itemInstance : this.ahC) {
            this.writeH(itemInstance.getTemplate().getType1());
            this.writeD(itemInstance.getObjectId());
            this.writeD(itemInstance.getItemId());
            this.writeD((int)itemInstance.getCount());
            this.writeH(itemInstance.getTemplate().getType2ForPackets());
            this.writeH(itemInstance.getBlessed());
            if (itemInstance.getTemplate().getType1() < 4) {
                this.writeD(itemInstance.getTemplate().getBodyPart());
                this.writeH(itemInstance.getEnchantLevel());
                this.writeH(itemInstance.getDamaged());
                this.writeH(0);
                this.writeD(itemInstance.getVariationStat1());
                this.writeD(itemInstance.getVariationStat2());
            }
            this.writeD(itemInstance.getObjectId());
        }
    }
}

