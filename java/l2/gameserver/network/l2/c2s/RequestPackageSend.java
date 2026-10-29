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
import l2.gameserver.model.items.PcFreight;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;

public class RequestPackageSend
extends L2GameClientPacket {
    private static final long ack = 1000L;
    private int CS;
    private int _count;
    private int[] abp;
    private int[] acl;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this._count = this.readD();
        if (this._count * 8 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.acl = new int[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.acl[i] = this.readD();
            if (this.acl[i] >= 1 && ArrayUtils.indexOf(this.abp, this.abp[i]) >= i) continue;
            this._count = 0;
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() throws Exception {
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
            player.sendPacket((IStaticPacket)SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
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
        if (!(npcInstance != null && npcInstance.isInActingRange(npcInstance) || Config.ALT_ALLOW_REMOTE_USE_CARGO_BOX)) {
            player.sendPacket((IStaticPacket)Msg.WAREHOUSE_IS_TOO_FAR);
            return;
        }
        if (!player.getAccountChars().containsKey(this.CS)) {
            return;
        }
        PcInventory pcInventory = player.getInventory();
        PcFreight pcFreight = new PcFreight(this.CS);
        pcFreight.restore();
        pcInventory.writeLock();
        pcFreight.writeLock();
        try {
            int n = 0;
            long l = 0L;
            n = Config.FREIGHT_SLOTS - pcFreight.getSize();
            int n2 = 0;
            for (int i = 0; i < this._count; ++i) {
                ItemInstance itemInstance = pcInventory.getItemByObjectId(this.abp[i]);
                if (itemInstance == null || itemInstance.getCount() < (long)this.acl[i] || !itemInstance.getTemplate().isTradeable() || !RequestPackageSend.CanSendItem(itemInstance)) {
                    this.abp[i] = 0;
                    this.acl[i] = 0;
                    continue;
                }
                if (!itemInstance.isStackable() || pcFreight.getItemByItemId(itemInstance.getItemId()) == null) {
                    if (n <= 0) {
                        this.abp[i] = 0;
                        this.acl[i] = 0;
                        continue;
                    }
                    --n;
                }
                if (itemInstance.getItemId() == 57) {
                    l = this.acl[i];
                }
                ++n2;
            }
            if (n <= 0) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            }
            if (n2 == 0) {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                return;
            }
            long l2 = SafeMath.mulAndCheck((long)n2, 1000L);
            if (l2 + l > player.getAdena()) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION);
                return;
            }
            if (!player.reduceAdena(l2, true)) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            for (int i = 0; i < this._count; ++i) {
                if (this.abp[i] == 0) continue;
                ItemInstance itemInstance = pcInventory.removeItemByObjectId(this.abp[i], this.acl[i]);
                Log.LogItem(player, Log.ItemLog.FreightDeposit, itemInstance);
                pcFreight.addItem(itemInstance);
            }
        }
        catch (ArithmeticException arithmeticException) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        finally {
            pcFreight.writeUnlock();
            pcInventory.writeUnlock();
        }
        player.sendChanges();
        player.sendPacket((IStaticPacket)SystemMsg.THE_TRANSACTION_IS_COMPLETE);
    }

    public static boolean CanSendItem(ItemInstance itemInstance) {
        if (!itemInstance.getTemplate().isTradeable()) {
            return false;
        }
        return !itemInstance.isEquipped() && !itemInstance.getTemplate().isQuest() && !itemInstance.isAugmented();
    }
}

