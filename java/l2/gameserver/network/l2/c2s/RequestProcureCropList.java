/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.math.SafeMath;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.ManorManagerInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.manor.CropProcure;
import org.apache.commons.lang3.ArrayUtils;

public class RequestProcureCropList
extends L2GameClientPacket {
    private int _count;
    private int[] abp;
    private int[] acB;
    private int[] acC;
    private long[] abq;

    @Override
    protected void readImpl() {
        this._count = this.readD();
        if (this._count * 16 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.acB = new int[this._count];
        this.acC = new int[this._count];
        this.abq = new long[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.acB[i] = this.readD();
            this.acC[i] = this.readD();
            this.abq[i] = this.readD();
            if (this.acB[i] >= 1 && this.acC[i] >= 1 && this.abq[i] >= 1L && ArrayUtils.indexOf(this.abp, this.abp[i]) >= i) continue;
            this._count = 0;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        CropProcure cropProcure;
        Castle castle;
        ItemInstance itemInstance;
        long l;
        int n;
        int n2;
        int n3;
        int n4;
        ManorManagerInstance manorManagerInstance;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this._count == 0) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendPacket((IStaticPacket)Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (player.isInTrade()) {
            player.sendActionFailed();
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && !player.isGM()) {
            player.sendActionFailed();
            return;
        }
        GameObject gameObject = player.getTarget();
        ManorManagerInstance manorManagerInstance2 = manorManagerInstance = gameObject != null && gameObject instanceof ManorManagerInstance ? (ManorManagerInstance)gameObject : null;
        if (!(player.isGM() || manorManagerInstance != null && manorManagerInstance.isInActingRange(player))) {
            player.sendActionFailed();
            return;
        }
        int n5 = manorManagerInstance == null ? 0 : manorManagerInstance.getCastle().getId();
        long l2 = 0L;
        int n6 = 0;
        long l3 = 0L;
        try {
            for (n4 = 0; n4 < this._count; ++n4) {
                n3 = this.abp[n4];
                n2 = this.acB[n4];
                n = this.acC[n4];
                l = this.abq[n4];
                itemInstance = player.getInventory().getItemByObjectId(n3);
                if (itemInstance == null || itemInstance.getCount() < l || itemInstance.getItemId() != n2) {
                    return;
                }
                castle = ResidenceHolder.getInstance().getResidence(Castle.class, n);
                if (castle == null) {
                    return;
                }
                cropProcure = castle.getCrop(n2, 0);
                if (cropProcure == null || cropProcure.getId() == 0 || cropProcure.getPrice() == 0L) {
                    return;
                }
                if (l > cropProcure.getAmount()) {
                    return;
                }
                long l4 = SafeMath.mulAndCheck(l, cropProcure.getPrice());
                long l5 = 0L;
                if (n5 != 0 && n != n5) {
                    l5 = l4 * 5L / 100L;
                }
                l2 = SafeMath.addAndCheck(l2, l5);
                int n7 = Manor.getInstance().getRewardItem(n2, cropProcure.getReward());
                ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n7);
                if (itemTemplate == null) {
                    return;
                }
                l3 = SafeMath.addAndCheck(l3, SafeMath.mulAndCheck(l, (long)itemTemplate.getWeight()));
                if (itemTemplate.isStackable() && player.getInventory().getItemByItemId(n2) != null) continue;
                ++n6;
            }
        }
        catch (ArithmeticException arithmeticException) {
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        player.getInventory().writeLock();
        try {
            if (!player.getInventory().validateWeight(l3)) {
                this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }
            if (!player.getInventory().validateCapacity(n6)) {
                this.sendPacket((L2GameServerPacket)Msg.YOUR_INVENTORY_IS_FULL);
                return;
            }
            if (player.getInventory().getAdena() < l2) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            for (n4 = 0; n4 < this._count; ++n4) {
                n3 = this.abp[n4];
                n2 = this.acB[n4];
                n = this.acC[n4];
                l = this.abq[n4];
                itemInstance = player.getInventory().getItemByObjectId(n3);
                if (itemInstance == null || itemInstance.getCount() < l || itemInstance.getItemId() != n2 || (castle = ResidenceHolder.getInstance().getResidence(Castle.class, n)) == null || (cropProcure = castle.getCrop(n2, 0)) == null || cropProcure.getId() == 0 || cropProcure.getPrice() == 0L || l > cropProcure.getAmount()) continue;
                int n8 = Manor.getInstance().getRewardItem(n2, cropProcure.getReward());
                long l6 = l * cropProcure.getPrice();
                long l7 = ItemHolder.getInstance().getTemplate(n8).getReferencePrice();
                if (l7 == 0L) continue;
                double d = (double)l6 / (double)l7;
                long l8 = (long)d + (long)(Rnd.nextDouble() <= d % 1.0 ? 1 : 0);
                if (l8 < 1L) {
                    SystemMessage systemMessage = new SystemMessage(1491);
                    systemMessage.addItemName(n2);
                    systemMessage.addNumber(l);
                    player.sendPacket((IStaticPacket)systemMessage);
                    continue;
                }
                long l9 = 0L;
                if (n5 != 0 && n != n5) {
                    l9 = l6 * 5L / 100L;
                }
                if (!player.getInventory().destroyItemByObjectId(n3, l)) continue;
                if (!player.reduceAdena(l9, false)) {
                    SystemMessage systemMessage = new SystemMessage(1491);
                    systemMessage.addItemName(n2);
                    systemMessage.addNumber(l);
                    player.sendPacket(systemMessage, Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    continue;
                }
                cropProcure.setAmount(cropProcure.getAmount() - l);
                castle.updateCrop(cropProcure.getId(), cropProcure.getAmount(), 0);
                castle.addToTreasuryNoTax(l9, false, false);
                if (player.getInventory().addItem(n8, l8) == null) continue;
                player.sendPacket(new SystemMessage(1490).addItemName(n2).addNumber(l), SystemMessage2.removeItems(n2, l), SystemMessage2.obtainItems(n8, l8, 0));
                if (l9 <= 0L) continue;
                player.sendPacket((IStaticPacket)new SystemMessage(1607).addNumber(l9));
            }
        }
        finally {
            player.getInventory().writeUnlock();
        }
        player.sendChanges();
    }
}

