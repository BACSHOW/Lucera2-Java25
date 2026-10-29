/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.promoCode;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.promoCode.PromoCodeReward;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import org.dom4j.Element;

public class ItemPromoCodeReward
extends PromoCodeReward {
    public int _itemId;
    public int _itemCount;
    private int Xh;

    public ItemPromoCodeReward(Element element) {
        this._itemId = Integer.parseInt(element.attributeValue("id"));
        this._itemCount = Integer.parseInt(element.attributeValue("count", "1"));
        this.Xh = Integer.parseInt(element.attributeValue("enchant", "0"));
    }

    @Override
    public void giveReward(Player player) {
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(this._itemId);
        if (itemTemplate.isStackable()) {
            player.getInventory().addItem(this._itemId, this._itemCount);
        } else {
            InventoryUpdate inventoryUpdate = new InventoryUpdate();
            for (long i = 0L; i < (long)this._itemCount; ++i) {
                ItemInstance itemInstance = player.getInventory().addItem(this._itemId, 1L);
                if (this.Xh > 0) {
                    itemInstance.setEnchantLevel(this.Xh);
                }
                inventoryUpdate.addModifiedItem(itemInstance);
            }
            player.sendPacket((IStaticPacket)inventoryUpdate);
        }
        player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(this._itemId, this._itemCount, this.Xh));
    }
}

