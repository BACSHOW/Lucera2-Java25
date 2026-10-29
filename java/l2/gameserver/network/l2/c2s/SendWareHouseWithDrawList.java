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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendWareHouseWithDrawList
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(SendWareHouseWithDrawList.class);
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
            break;
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
        if (player.isTradeBannedByGM()) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_TRADE_ITEMS);
            return;
        }
        NpcInstance npcInstance = player.getLastNpc();
        if (!(npcInstance != null && npcInstance.isInActingRange(player) || Config.ALT_ALLOW_REMOTE_USE_CARGO_BOX)) {
            player.sendPacket((IStaticPacket)Msg.WAREHOUSE_IS_TOO_FAR);
            return;
        }
        Warehouse warehouse = null;
        Log.ItemLog itemLog = null;
        if (player.getUsingWarehouseType() == Warehouse.WarehouseType.PRIVATE) {
            warehouse = player.getWarehouse();
            itemLog = Log.ItemLog.WarehouseWithdraw;
        } else if (player.getUsingWarehouseType() == Warehouse.WarehouseType.CLAN) {
            itemLog = Log.ItemLog.ClanWarehouseWithdraw;
            if (player.getClan() == null || player.getClan().getLevel() == 0) {
                player.sendPacket((IStaticPacket)Msg.ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE);
                return;
            }
            boolean bl = false;
            if (player.getClan() != null && (player.getClanPrivileges() & 8) == 8 && (Config.ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE || player.isClanLeader() || player.getVarB("canWhWithdraw"))) {
                bl = true;
            }
            if (!bl) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE);
                return;
            }
            warehouse = player.getClan().getWarehouse();
        } else if (player.getUsingWarehouseType() == Warehouse.WarehouseType.FREIGHT) {
            warehouse = player.getFreight();
            itemLog = Log.ItemLog.FreightWithdraw;
        } else {
            _log.warn("Error retrieving a warehouse object for char " + player.getName() + " - using warehouse type: " + (Object)((Object)player.getUsingWarehouseType()));
            return;
        }
        PcInventory pcInventory = player.getInventory();
        pcInventory.writeLock();
        warehouse.writeLock();
        try {
            ItemInstance itemInstance;
            int n;
            long l = 0L;
            int n2 = 0;
            for (n = 0; n < this._count; ++n) {
                itemInstance = warehouse.getItemByObjectId(this.abp[n]);
                if (itemInstance == null || itemInstance.getCount() < this.abq[n]) {
                    player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                    return;
                }
                l = SafeMath.addAndCheck(l, SafeMath.mulAndCheck((long)itemInstance.getTemplate().getWeight(), this.abq[n]));
                if (itemInstance.isStackable() && pcInventory.getItemByItemId(itemInstance.getItemId()) != null) continue;
                ++n2;
            }
            if (!player.getInventory().validateCapacity(n2)) {
                player.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
                return;
            }
            if (!player.getInventory().validateWeight(l)) {
                player.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }
            for (n = 0; n < this._count; ++n) {
                itemInstance = warehouse.removeItemByObjectId(this.abp[n], this.abq[n]);
                Log.LogItem(player, itemLog, itemInstance);
                player.getInventory().addItem(itemInstance);
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

