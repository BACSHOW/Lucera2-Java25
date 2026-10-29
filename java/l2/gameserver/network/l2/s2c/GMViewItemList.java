/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class GMViewItemList
extends L2GameServerPacket {
    private int _size;
    private ItemInstance[] ahC;
    private int KH;
    private String _name;

    public GMViewItemList(Player player, ItemInstance[] itemInstanceArray, int n) {
        this._size = n;
        this.ahC = itemInstanceArray;
        this._name = player.getName();
        this.KH = player.getInventoryLimit();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(148);
        this.writeS(this._name);
        this.writeD(this.KH);
        this.writeH(1);
        this.writeH(this.ahC.length);
        for (ItemInstance itemInstance : this.ahC) {
            this.writeH(itemInstance.getTemplate().getType1());
            this.writeD(itemInstance.getObjectId());
            this.writeD(itemInstance.getItemId());
            this.writeD((int)itemInstance.getCount());
            this.writeH(itemInstance.getTemplate().getType2ForPackets());
            this.writeH(itemInstance.getBlessed());
            this.writeH(itemInstance.isEquipped() ? 1 : 0);
            this.writeD(itemInstance.getBodyPart());
            this.writeH(itemInstance.getEnchantLevel());
            this.writeH(itemInstance.getTemplate().getType2());
            this.writeH(itemInstance.getVariationStat1());
            this.writeH(itemInstance.getVariationStat2());
            this.writeD(itemInstance.getDuration());
        }
    }
}

