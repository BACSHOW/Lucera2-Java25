/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PetItemList
extends L2GameServerPacket {
    private ItemInstance[] akT;

    public PetItemList(PetInstance petInstance) {
        this.akT = petInstance.getInventory().getItems();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(178);
        this.writeH(this.akT.length);
        for (ItemInstance itemInstance : this.akT) {
            this.writeH(itemInstance.getTemplate().getType1());
            this.writeD(itemInstance.getObjectId());
            this.writeD(itemInstance.getItemId());
            this.writeD((int)itemInstance.getCount());
            this.writeH(itemInstance.getTemplate().getType2ForPackets());
            this.writeH(itemInstance.getBlessed());
            this.writeH(itemInstance.isEquipped() ? 1 : 0);
            this.writeD(itemInstance.getTemplate().getBodyPart());
            this.writeH(itemInstance.getEnchantLevel());
            this.writeH(itemInstance.getDamaged());
        }
    }
}

