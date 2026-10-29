/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBuyItem
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestBuyItem.class);
    private int wr;
    private int _count;
    private int[] abp;
    private long[] abq;

    @Override
    protected void readImpl() {
        this.wr = this.readD();
        this._count = this.readD();
        if (this._count * 8 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.abq = new long[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.abq[i] = this.readD();
            if (this.abq[i] >= 1L) continue;
            this._count = 0;
            break;
        }
    }

    @Override
    protected void runImpl() {
        boolean bl;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this._count == 0) {
            return;
        }
        if (player.getBuyListId() != this.wr) {
            player.sendActionFailed();
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
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.getKarma() > 0 && !player.isGM()) {
            player.sendActionFailed();
            return;
        }
        NpcInstance npcInstance = player.getLastNpc();
        boolean bl2 = bl = npcInstance != null && npcInstance.isMerchantNpc();
        if (!(player.isGM() || npcInstance != null && bl && npcInstance.isInActingRange(player))) {
            player.sendActionFailed();
            return;
        }
        BuyListHolder.NpcTradeList npcTradeList = BuyListHolder.getInstance().getBuyList(this.wr);
        if (npcTradeList == null) {
            player.sendActionFailed();
            return;
        }
        int n = 0;
        long l = 0L;
        long l2 = 0L;
        long l3 = 0L;
        double d = 0.0;
        Castle castle = null;
        if (npcInstance != null && (castle = npcInstance.getCastle(player)) != null) {
            d = castle.getTaxRate();
        }
        ArrayList<TradeItem> arrayList = new ArrayList<TradeItem>(this._count);
        List<TradeItem> list = npcTradeList.getItems();
        try {
            block2: for (int i = 0; i < this._count; ++i) {
                int n2 = this.abp[i];
                long l4 = this.abq[i];
                long l5 = 0L;
                for (TradeItem tradeItem : list) {
                    if (tradeItem.getItemId() != n2) continue;
                    if (tradeItem.isCountLimited() && tradeItem.getCurrentValue() < l4) continue block2;
                    l5 = tradeItem.getOwnersPrice();
                }
                if (!(l5 != 0L || player.isGM() && player.getPlayerAccess().UseGMShop)) {
                    player.sendActionFailed();
                    return;
                }
                l2 = SafeMath.addAndCheck(l2, SafeMath.mulAndCheck(l4, l5));
                TradeItem tradeItem = new TradeItem();
                tradeItem.setItemId(n2);
                tradeItem.setCount(l4);
                tradeItem.setOwnersPrice(l5);
                l = SafeMath.addAndCheck(l, SafeMath.mulAndCheck(l4, (long)tradeItem.getItem().getWeight()));
                if (!tradeItem.getItem().isStackable() || player.getInventory().getItemByItemId(n2) == null) {
                    ++n;
                }
                arrayList.add(tradeItem);
            }
            l3 = (long)((double)l2 * d);
            l2 = SafeMath.addAndCheck(l2, l3);
            if (!player.getInventory().validateWeight(l)) {
                this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }
            if (!player.getInventory().validateCapacity(n)) {
                this.sendPacket((L2GameServerPacket)Msg.YOUR_INVENTORY_IS_FULL);
                return;
            }
            if (!player.reduceAdena(l2)) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            for (TradeItem tradeItem : arrayList) {
                player.getInventory().addItem(tradeItem.getItemId(), tradeItem.getCount());
            }
            npcTradeList.updateItems(arrayList);
            if (castle != null && l3 > 0L && castle.getOwnerId() > 0 && player.getReflection() == ReflectionManager.DEFAULT) {
                castle.addToTreasury(l3, true, false);
            }
        }
        catch (ArithmeticException arithmeticException) {
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        player.sendChanges();
    }
}

