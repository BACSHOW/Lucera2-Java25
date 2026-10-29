/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListBuy;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgBuy;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.TradeHelper;

public class SetPrivateStoreBuyList
extends L2GameClientPacket {
    private int _count;
    private List<SetPrivateStoreBuyListEntry> adh = Collections.emptyList();

    @Override
    protected void readImpl() {
        this._count = this.readD();
        if (this._count < 1 || this._count * 16 != this._buf.remaining() || this._count > 1024) {
            this._count = 0;
            return;
        }
        ArrayList<SetPrivateStoreBuyListEntry> arrayList = new ArrayList<SetPrivateStoreBuyListEntry>(this._count);
        for (int i = 0; i < this._count; ++i) {
            int n = this.readD();
            int n2 = this.readH();
            int n3 = this.readH();
            long l = this.readD();
            long l2 = this.readD();
            if (l < 1L || l2 < 1L) {
                arrayList.clear();
                return;
            }
            arrayList.add(new SetPrivateStoreBuyListEntry(n, n2, n3, l, l2));
        }
        this.adh = arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this._count == 0 || this._count != this.adh.size()) {
            return;
        }
        if (player.isMoving()) {
            player.stopMove();
        }
        if (!TradeHelper.checksIfCanOpenStore(player, 3)) {
            player.sendActionFailed();
            return;
        }
        player.getInventory().readLock();
        CopyOnWriteArrayList<TradeItem> copyOnWriteArrayList = new CopyOnWriteArrayList<TradeItem>();
        long l = 0L;
        int n = 0;
        try {
            for (SetPrivateStoreBuyListEntry setPrivateStoreBuyListEntry : this.adh) {
                ItemTemplate itemTemplate;
                if (setPrivateStoreBuyListEntry.getItemId() == 57 || (itemTemplate = ItemHolder.getInstance().getTemplate(setPrivateStoreBuyListEntry.getItemId())) == null || player.getInventory().getItemByItemId(itemTemplate.getItemId()) == null) continue;
                l = SafeMath.addAndCheck(l, SafeMath.mulAndCheck(setPrivateStoreBuyListEntry.getCount(), setPrivateStoreBuyListEntry.getPrice()));
                n = SafeMath.addAndCheck(n, SafeMath.mulAndCheck((int)setPrivateStoreBuyListEntry.getCount(), itemTemplate.getWeight()));
                TradeItem tradeItem = new TradeItem();
                tradeItem.setItemId(setPrivateStoreBuyListEntry.getItemId());
                tradeItem.setEnchantLevel(setPrivateStoreBuyListEntry.getEnchant());
                tradeItem.setCount(setPrivateStoreBuyListEntry.getCount());
                tradeItem.setOwnersPrice(setPrivateStoreBuyListEntry.getPrice());
                tradeItem.setReferencePrice(itemTemplate.getReferencePrice());
                if (tradeItem.getStorePrice() > setPrivateStoreBuyListEntry.getPrice()) {
                    player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.SetPrivateStoreBuyList.TooLowPrice", player, new Object[0]).addItemName(itemTemplate).addNumber((long)itemTemplate.getReferencePrice() / Config.ALT_PRIVATE_STORE_BUY_DIVISOR));
                    continue;
                }
                copyOnWriteArrayList.add(tradeItem);
            }
        }
        catch (ArithmeticException arithmeticException) {
            copyOnWriteArrayList.clear();
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        finally {
            player.getInventory().readUnlock();
        }
        if (!player.getInventory().validateWeight(n)) {
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return;
        }
        if (!player.getInventory().validateCapacity(copyOnWriteArrayList.size())) {
            this.sendPacket((L2GameServerPacket)Msg.YOUR_INVENTORY_IS_FULL);
            return;
        }
        if (copyOnWriteArrayList.size() > player.getTradeLimit() || l > Integer.MAX_VALUE || copyOnWriteArrayList.size() != this._count) {
            player.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            player.sendPacket((IStaticPacket)new PrivateStoreManageListBuy(player));
            return;
        }
        if (l > player.getAdena()) {
            player.sendPacket((IStaticPacket)Msg.THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE);
            player.sendPacket((IStaticPacket)new PrivateStoreManageListBuy(player));
            return;
        }
        player.getInventory().writeLock();
        try {
            if (!copyOnWriteArrayList.isEmpty()) {
                player.setBuyList(copyOnWriteArrayList);
                player.setPrivateStoreType(3);
                player.broadcastPacket(new PrivateStoreMsgBuy(player));
                player.saveTradeList();
            } else {
                player.setBuyList(Collections.emptyList());
                player.setPrivateStoreType(0);
            }
        }
        finally {
            player.getInventory().writeUnlock();
        }
        player.sendActionFailed();
    }

    private static class SetPrivateStoreBuyListEntry {
        public static final int IN_PACKET_SIZE = 16;
        private final int itemId;
        private final int enchant;
        private final int damage;
        private final long count;
        private final long price;

        private SetPrivateStoreBuyListEntry(int n, int n2, int n3, long l, long l2) {
            this.itemId = n;
            this.enchant = n2;
            this.damage = n3;
            this.count = l;
            this.price = l2;
        }

        public int getItemId() {
            return this.itemId;
        }

        public int getEnchant() {
            return this.enchant;
        }

        public int getDamage() {
            return this.damage;
        }

        public long getCount() {
            return this.count;
        }

        public long getPrice() {
            return this.price;
        }
    }
}

