/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;
import l2.commons.math.SafeMath;
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

public class RequestPrivateStoreManageBuy
extends L2GameClientPacket {
    private int acz;
    private int _count;
    private int[] abp;
    private long[] abq;
    private long[] acy;

    @Override
    protected void readImpl() {
        this.acz = this.readD();
        this._count = this.readD();
        if (this._count * 12 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.abq = new long[this._count];
        this.acy = new long[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.abq[i] = this.readD();
            this.acy[i] = this.readD();
            if (this.abq[i] >= 1L && this.acy[i] >= 1L && ArrayUtils.indexOf(this.abp, this.abp[i]) >= i) continue;
            this._count = 0;
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        List<TradeItem> list;
        Player player;
        Player player2;
        block49: {
            player2 = ((GameClient)this.getClient()).getActiveChar();
            if (player2 == null || this._count == 0) {
                return;
            }
            if (player2.isActionsDisabled()) {
                player2.sendActionFailed();
                return;
            }
            if (player2.isInStoreMode()) {
                player2.sendPacket((IStaticPacket)Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
                return;
            }
            if (player2.isInTrade()) {
                player2.sendActionFailed();
                return;
            }
            if (player2.isFishing()) {
                player2.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING);
                return;
            }
            if (!player2.getPlayerAccess().UseTrade) {
                player2.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
                return;
            }
            player = (Player)player2.getVisibleObject(this.acz);
            if (player == null || player.getPrivateStoreType() != 1 && player.getPrivateStoreType() != 8 || !player.isInActingRange(player2)) {
                player2.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
                player2.sendActionFailed();
                return;
            }
            list = player.getSellList();
            if (list.isEmpty()) {
                player2.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
                player2.sendActionFailed();
                return;
            }
            ArrayList<TradeItem> arrayList = new ArrayList<TradeItem>();
            long l = 0L;
            int n = 0;
            long l2 = 0L;
            player2.getInventory().writeLock();
            player.getInventory().writeLock();
            try {
                block27: for (int i = 0; i < this._count; ++i) {
                    int n2 = this.abp[i];
                    long l3 = this.abq[i];
                    long l4 = this.acy[i];
                    TradeItem tradeItem = null;
                    for (TradeItem tradeItem2 : list) {
                        if (tradeItem2.getObjectId() != n2 || tradeItem2.getOwnersPrice() != l4) continue;
                        if (l3 > tradeItem2.getCount()) {
                            break block49;
                        }
                        ItemInstance itemInstance = player.getInventory().getItemByObjectId(n2);
                        if (itemInstance == null || itemInstance.getCount() < l3) break block49;
                        if (!itemInstance.canBeTraded(player)) {
                            break block49;
                        }
                        l = SafeMath.addAndCheck(l, SafeMath.mulAndCheck(l3, l4));
                        l2 = SafeMath.addAndCheck(l2, SafeMath.mulAndCheck(l3, (long)itemInstance.getTemplate().getWeight()));
                        if (!itemInstance.isStackable() || player2.getInventory().getItemByItemId(itemInstance.getItemId()) == null) {
                            ++n;
                        }
                        tradeItem = new TradeItem();
                        tradeItem.setObjectId(n2);
                        tradeItem.setItemId(itemInstance.getItemId());
                        tradeItem.setCount(l3);
                        tradeItem.setOwnersPrice(l4);
                        arrayList.add(tradeItem);
                        continue block27;
                    }
                }
            }
            catch (ArithmeticException arithmeticException) {
                arrayList.clear();
                this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
                return;
            }
            finally {
                try {
                    if (arrayList.size() != this._count || player.getPrivateStoreType() == 8 && arrayList.size() != list.size()) {
                        player2.sendPacket((IStaticPacket)Msg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
                        player2.sendActionFailed();
                        return;
                    }
                    if (!player2.getInventory().validateWeight(l2)) {
                        player2.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                        player2.sendActionFailed();
                        return;
                    }
                    if (!player2.getInventory().validateCapacity(n)) {
                        player2.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
                        player2.sendActionFailed();
                        return;
                    }
                    if (!player2.reduceAdena(l)) {
                        player2.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        player2.sendActionFailed();
                        return;
                    }
                    if (player2.isTradeBannedByGM()) {
                        player2.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
                        player2.sendActionFailed();
                        return;
                    }
                    for (TradeItem tradeItem : arrayList) {
                        ItemInstance itemInstance = player.getInventory().removeItemByObjectId(tradeItem.getObjectId(), tradeItem.getCount());
                        for (TradeItem tradeItem3 : list) {
                            if (tradeItem3.getObjectId() != tradeItem.getObjectId()) continue;
                            tradeItem3.setCount(tradeItem3.getCount() - tradeItem.getCount());
                            if (tradeItem3.getCount() >= 1L) break;
                            list.remove(tradeItem3);
                            break;
                        }
                        player2.getInventory().addItem(itemInstance);
                        TradeHelper.purchaseItem(player2, player, tradeItem);
                    }
                    long l5 = TradeHelper.getTax(player, l);
                    if (l5 > 0L) {
                        l -= l5;
                        player.sendMessage(new CustomMessage("trade.HavePaidTax", player, new Object[0]).addNumber(l5));
                    }
                    player.addAdena(l);
                    player.saveTradeList();
                }
                finally {
                    player.getInventory().writeUnlock();
                    player2.getInventory().writeUnlock();
                }
            }
        }
        if (list.isEmpty()) {
            TradeHelper.cancelStore(player);
        }
        player.sendChanges();
        player2.sendChanges();
        player2.sendActionFailed();
    }
}

