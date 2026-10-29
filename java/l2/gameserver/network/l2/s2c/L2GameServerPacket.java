/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.commons.net.nio.impl.SendablePacket;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.base.MultiSellIngredient;
import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Log;

public abstract class L2GameServerPacket
extends SendablePacket<GameClient>
implements IStaticPacket {
    @Override
    public final boolean write() {
        try {
            this.writeImpl();
            if (Config.DEBUG_PKG) {
                System.out.println(System.currentTimeMillis() + ":SEND: " + this.getClass().getSimpleName());
            }
            boolean bl = true;
            return bl;
        }
        catch (Exception exception) {
            Log.network("Client: " + this.getClient() + " - Failed writing: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), exception);
        }
        return false;
    }

    protected abstract void writeImpl();

    protected void writeEx(int n) {
        this.writeC(254);
        this.writeH(n);
    }

    protected void writeD(boolean bl) {
        this.writeD(bl ? 1 : 0);
    }

    protected void writeDD(int[] nArray, boolean bl) {
        if (bl) {
            this.getByteBuffer().putInt(nArray.length);
        }
        for (int n : nArray) {
            this.getByteBuffer().putInt(n);
        }
    }

    protected void writeDD(int[] nArray) {
        this.writeDD(nArray, false);
    }

    protected void writeItemInfo(ItemInstance itemInstance) {
        this.writeItemInfo(itemInstance, itemInstance.getCount());
    }

    protected void writeItemInfo(ItemInstance itemInstance, long l) {
        this.writeH(itemInstance.getTemplate().getType1());
        this.writeD(itemInstance.getObjectId());
        this.writeD(itemInstance.getItemId());
        this.writeD((int)l);
        this.writeH(itemInstance.getTemplate().getType2ForPackets());
        this.writeH(itemInstance.getBlessed());
        this.writeH(itemInstance.isEquipped() ? 1 : 0);
        this.writeD(itemInstance.getTemplate().getBodyPart());
        this.writeH(itemInstance.getEnchantLevel());
        this.writeH(itemInstance.getDamaged());
        this.writeH(itemInstance.getVariationStat1());
        this.writeH(itemInstance.getVariationStat2());
        this.writeD(itemInstance.getDuration());
    }

    protected void writeItemInfo(ItemInfo itemInfo) {
        this.writeItemInfo(itemInfo, itemInfo.getCount());
    }

    protected void writeItemInfo(ItemInfo itemInfo, long l) {
        this.writeH(itemInfo.getItem().getType1());
        this.writeD(itemInfo.getObjectId());
        this.writeD(itemInfo.getItemId());
        this.writeD((int)l);
        this.writeH(itemInfo.getItem().getType2ForPackets());
        this.writeH(itemInfo.getCustomType1());
        this.writeH(itemInfo.isEquipped() ? 1 : 0);
        this.writeD(itemInfo.getItem().getBodyPart());
        this.writeH(itemInfo.getEnchantLevel());
        this.writeH(itemInfo.getCustomType2());
        this.writeH(itemInfo.getVariationStat1());
        this.writeH(itemInfo.getVariationStat2());
        this.writeD(itemInfo.getShadowLifeTime());
    }

    protected void writeItemElements(MultiSellIngredient multiSellIngredient) {
        if (multiSellIngredient.getItemId() <= 0) {
            this.writeItemElements();
            return;
        }
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(multiSellIngredient.getItemId());
        if (multiSellIngredient.getItemAttributes().getValue() > 0) {
            if (itemTemplate.isWeapon()) {
                Element element = multiSellIngredient.getItemAttributes().getElement();
                this.writeH(element.getId());
                this.writeH(multiSellIngredient.getItemAttributes().getValue(element) + itemTemplate.getBaseAttributeValue(element));
                this.writeH(0);
                this.writeH(0);
                this.writeH(0);
                this.writeH(0);
                this.writeH(0);
                this.writeH(0);
            } else if (itemTemplate.isArmor()) {
                this.writeH(-1);
                this.writeH(0);
                for (Element element : Element.VALUES) {
                    this.writeH(multiSellIngredient.getItemAttributes().getValue(element) + itemTemplate.getBaseAttributeValue(element));
                }
            } else {
                this.writeItemElements();
            }
        } else {
            this.writeItemElements();
        }
    }

    protected void writeItemElements() {
        this.writeH(-1);
        this.writeH(0);
        this.writeH(0);
        this.writeH(0);
        this.writeH(0);
        this.writeH(0);
        this.writeH(0);
        this.writeH(0);
    }

    public String getType() {
        return "[S] " + this.getClass().getSimpleName();
    }

    @Override
    public L2GameServerPacket packet(Player player) {
        return this;
    }
}

