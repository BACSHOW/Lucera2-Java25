/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.TradeHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestPrivateStoreBuySellList
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestPrivateStoreBuySellList.class);
    private int acx;
    private int _count;
    private int[] abp;
    private long[] abq;
    private long[] acy;

    @Override
    protected void readImpl() {
        this.acx = this.readD();
        this._count = this.readD();
        if (this._count * 20 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.abq = new long[this._count];
        this.acy = new long[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.readD();
            this.readH();
            this.readH();
            this.abq[i] = this.readD();
            this.acy[i] = this.readD();
            if (this.abq[i] >= 1L && this.acy[i] >= 1L && ArrayUtils.indexOf(this.abp, this.abp[i]) >= i) continue;
            this._count = 0;
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) return;
        if (this._count == 0) {
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
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
            return;
        }
        if (player.isTradeBannedByGM()) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            return;
        }
        if (!player.getPlayerAccess().UseTrade) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
            return;
        }
        Player player2 = (Player)player.getVisibleObject(this.acx);
        if (player2 == null || player2.getPrivateStoreType() != 3 || !player.isInActingRange(player2)) {
            player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
            player.sendActionFailed();
            return;
        }
        List<TradeItem> list = player2.getBuyList();
        if (list.isEmpty()) {
            player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
            player.sendActionFailed();
            return;
        }
        ArrayList<TradeItem> arrayList = new ArrayList<TradeItem>();
        long l = 0L;
        int n = 0;
        long l2 = 0L;
        player2.getInventory().writeLock();
        player.getInventory().writeLock();
        try {
            List<TradeItem> remainingBuyList = new ArrayList<TradeItem>(list);
            block25: for (int i = 0; i < this._count; ++i) {
                int n2 = this.abp[i];
                long l3 = this.abq[i];
                long l4 = this.acy[i];
                ItemInstance itemInstance = player.getInventory().getItemByObjectId(n2);
                if (itemInstance == null || itemInstance.getCount() < l3 || !itemInstance.canBeTraded(player)) break;
                TradeItem tradeItem = null;
                Iterator iterator = remainingBuyList.iterator();
                while (iterator.hasNext()) {
                    TradeItem tradeItem2 = (TradeItem)iterator.next();
                    if (tradeItem2.getItemId() != itemInstance.getItemId() || tradeItem2.getOwnersPrice() != l4 || Config.PRIVATE_BUY_MATCH_ENCHANT && itemInstance.getEnchantLevel() != tradeItem2.getEnchantLevel() || !Config.PRIVATE_BUY_MATCH_ENCHANT && itemInstance.getEnchantLevel() < tradeItem2.getEnchantLevel()) continue;
                    if (l3 > tradeItem2.getCount()) break block25;
                    l = SafeMath.addAndCheck(l, SafeMath.mulAndCheck(l3, l4));
                    l2 = SafeMath.addAndCheck(l2, SafeMath.mulAndCheck(l3, (long)itemInstance.getTemplate().getWeight()));
                    if (!itemInstance.isStackable() || player2.getInventory().getItemByItemId(itemInstance.getItemId()) == null) {
                        ++n;
                    }
                    tradeItem = new TradeItem();
                    tradeItem.setObjectId(n2);
                    tradeItem.setItemId(itemInstance.getItemId());
                    tradeItem.setEnchantLevel(itemInstance.getEnchantLevel());
                    tradeItem.setCount(l3);
                    tradeItem.setOwnersPrice(l4);
                    arrayList.add(tradeItem);
                    iterator.remove();
                    continue block25;
                }
            }
            try {
                if (arrayList.size() != this._count) {
                    player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    player.sendActionFailed();
                    return;
                }
                if (!player2.getInventory().validateWeight(l2)) {
                    player2.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    player.sendActionFailed();
                    return;
                }
                if (!player2.getInventory().validateCapacity(n)) {
                    player2.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
                    player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    player.sendActionFailed();
                    return;
                }
                if (!player2.reduceAdena(l)) {
                    player2.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    player.sendActionFailed();
                    return;
                }
                for (TradeItem tradeItem : arrayList) {
                    ItemInstance transferredItem = player.getInventory().removeItemByObjectId(tradeItem.getObjectId(), tradeItem.getCount());
                    for (TradeItem tradeItem3 : list) {
                        if (tradeItem3.getItemId() != tradeItem.getItemId() || tradeItem3.getOwnersPrice() != tradeItem.getOwnersPrice() || Config.PRIVATE_BUY_MATCH_ENCHANT && transferredItem.getEnchantLevel() != tradeItem3.getEnchantLevel() || !Config.PRIVATE_BUY_MATCH_ENCHANT && transferredItem.getEnchantLevel() < tradeItem3.getEnchantLevel()) continue;
                        tradeItem3.setCount(tradeItem3.getCount() - tradeItem.getCount());
                        if (tradeItem3.getCount() >= 1L) break;
                        list.remove(tradeItem3);
                        break;
                    }
                    player2.getInventory().addItem(transferredItem);
                    TradeHelper.purchaseItem(player2, player, tradeItem);
                }
                long l5 = TradeHelper.getTax(player, l);
                if (l5 > 0L) {
                    l -= l5;
                    player.sendMessage(new CustomMessage("trade.HavePaidTax", player, new Object[0]).addNumber(l5));
                }
                player.addAdena(l);
                player2.saveTradeList();
            }
            finally {
                player.getInventory().writeUnlock();
                player2.getInventory().writeUnlock();
            }
        }
        catch (ArithmeticException arithmeticException) {
            arrayList.clear();
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        finally {
            try {
                if (arrayList.size() != this._count) {
                    player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    player.sendActionFailed();
                    return;
                }
                if (!player2.getInventory().validateWeight(l2)) {
                    player2.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                    player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    player.sendActionFailed();
                    return;
                }
                if (!player2.getInventory().validateCapacity(n)) {
                    player2.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
                    player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    player.sendActionFailed();
                    return;
                }
                if (!player2.reduceAdena(l)) {
                    player2.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    player.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_SELL_HAS_FAILED);
                    player.sendActionFailed();
                    return;
                }
                for (TradeItem tradeItem : arrayList) {
                    ItemInstance itemInstance = player.getInventory().removeItemByObjectId(tradeItem.getObjectId(), tradeItem.getCount());
                    for (TradeItem tradeItem4 : list) {
                        if (tradeItem4.getItemId() != tradeItem.getItemId() || tradeItem4.getOwnersPrice() != tradeItem.getOwnersPrice() || Config.PRIVATE_BUY_MATCH_ENCHANT && itemInstance.getEnchantLevel() != tradeItem4.getEnchantLevel() || !Config.PRIVATE_BUY_MATCH_ENCHANT && itemInstance.getEnchantLevel() < tradeItem4.getEnchantLevel()) continue;
                        tradeItem4.setCount(tradeItem4.getCount() - tradeItem.getCount());
                        if (tradeItem4.getCount() >= 1L) break;
                        list.remove(tradeItem4);
                        break;
                    }
                    player2.getInventory().addItem(itemInstance);
                    TradeHelper.purchaseItem(player2, player, tradeItem);
                }
                long l6 = TradeHelper.getTax(player, l);
                if (l6 > 0L) {
                    l -= l6;
                    player.sendMessage(new CustomMessage("trade.HavePaidTax", player, new Object[0]).addNumber(l6));
                }
                player.addAdena(l);
                player2.saveTradeList();
            }
            finally {
                player.getInventory().writeUnlock();
                player2.getInventory().writeUnlock();
            }
        }
        if (list.isEmpty()) {
            TradeHelper.cancelStore(player2);
        }
        player.sendChanges();
        player2.sendChanges();
        player.sendActionFailed();
    }
}

