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
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.model.items.Warehouse;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;

public class SendWareHouseDepositList
extends L2GameClientPacket {
    private static final long adg = 30L;
    private int _count;
    private int[] abp;
    private long[] abq;

    @Override
    protected void readImpl() {
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
            if (this.abq[i] >= 1L && ArrayUtils.indexOf(this.abp, this.abp[i]) >= i) continue;
            this._count = 0;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this._count == 0) {
            return;
        }
        if (!player.getPlayerAccess().UseWarehouse) {
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
        NpcInstance npcInstance = player.getLastNpc();
        if (npcInstance == null || !npcInstance.isInActingRange(player)) {
            player.sendPacket((IStaticPacket)Msg.WAREHOUSE_IS_TOO_FAR);
            return;
        }
        if (player.isTradeBannedByGM()) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            return;
        }
        PcInventory pcInventory = player.getInventory();
        boolean bl = player.getUsingWarehouseType() != Warehouse.WarehouseType.CLAN;
        Warehouse warehouse = bl ? player.getWarehouse() : player.getClan().getWarehouse();
        pcInventory.writeLock();
        warehouse.writeLock();
        try {
            int n = 0;
            long l = 0L;
            n = bl ? player.getWarehouseLimit() - warehouse.getSize() : player.getClan().getWhBonus() + Config.WAREHOUSE_SLOTS_CLAN - warehouse.getSize();
            int n2 = 0;
            for (int i = 0; i < this._count; ++i) {
                ItemInstance itemInstance = pcInventory.getItemByObjectId(this.abp[i]);
                if (itemInstance == null || itemInstance.getCount() < this.abq[i] || !itemInstance.canBeStored(player, bl)) {
                    this.abp[i] = 0;
                    this.abq[i] = 0L;
                    continue;
                }
                if (!itemInstance.isStackable() || warehouse.getItemByItemId(itemInstance.getItemId()) == null) {
                    if (n <= 0) {
                        this.abp[i] = 0;
                        this.abq[i] = 0L;
                        continue;
                    }
                    --n;
                }
                if (itemInstance.getItemId() == 57) {
                    l = this.abq[i];
                }
                ++n2;
            }
            if (n <= 0) {
                player.sendPacket((IStaticPacket)Msg.YOUR_WAREHOUSE_IS_FULL);
            }
            if (n2 == 0) {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                return;
            }
            long l2 = SafeMath.mulAndCheck((long)n2, 30L);
            if (l2 + l > player.getAdena()) {
                player.sendPacket((IStaticPacket)Msg.YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION);
                return;
            }
            if (!player.reduceAdena(l2, true)) {
                this.sendPacket((L2GameServerPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            for (int i = 0; i < this._count; ++i) {
                if (this.abp[i] == 0) continue;
                ItemInstance itemInstance = pcInventory.removeItemByObjectId(this.abp[i], this.abq[i]);
                Log.LogItem(player, bl ? Log.ItemLog.WarehouseDeposit : Log.ItemLog.ClanWarehouseDeposit, itemInstance);
                warehouse.addItem(itemInstance);
            }
        }
        catch (ArithmeticException arithmeticException) {
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        finally {
            warehouse.writeUnlock();
            pcInventory.writeUnlock();
        }
        player.sendChanges();
        player.sendPacket((IStaticPacket)Msg.THE_TRANSACTION_IS_COMPLETE);
    }
}

