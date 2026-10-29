/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SellRefundList;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;

public class RequestSellItem
extends L2GameClientPacket {
    private int wr;
    private int _count;
    private int[] abp;
    private long[] abq;

    @Override
    protected void readImpl() {
        this.wr = this.readD();
        this._count = this.readD();
        if (this._count * 12 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.abq = new long[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.readD();
            this.abq[i] = this.readD();
            if (this.abq[i] >= 1L && ArrayUtils.indexOf(this.abp, this.abp[i]) >= i) continue;
            this._count = 0;
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        boolean bl;
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
        if (!(Config.ALT_ALLOW_REMOTE_SELL_ITEMS_TO_SHOP || player.isGM() || npcInstance != null && bl && player.isInActingRange(npcInstance))) {
            player.sendActionFailed();
            return;
        }
        player.getInventory().writeLock();
        try {
            for (int i = 0; i < this._count; ++i) {
                int n = this.abp[i];
                long l = this.abq[i];
                ItemInstance itemInstance = player.getInventory().getItemByObjectId(n);
                if (itemInstance == null || itemInstance.getCount() < l || !itemInstance.canBeSold(player)) continue;
                long l2 = SafeMath.mulAndCheck(Math.max(0L, (long)itemInstance.getReferencePrice() / Config.ALT_SHOP_REFUND_SELL_DIVISOR), l);
                ItemInstance itemInstance2 = player.getInventory().removeItemByObjectId(n, l);
                Log.LogItem(player, Log.ItemLog.RefundSell, itemInstance2);
                player.addAdena(l2);
                player.getRefund().addItem(itemInstance2);
            }
        }
        catch (ArithmeticException arithmeticException) {
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        finally {
            player.getInventory().writeUnlock();
        }
        player.sendPacket((IStaticPacket)new SellRefundList(player, true));
        player.sendChanges();
    }
}

