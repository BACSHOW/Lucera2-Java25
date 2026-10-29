/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListSell;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgSell;
import l2.gameserver.utils.TradeHelper;
import org.apache.commons.lang3.ArrayUtils;

public class SetPrivateStoreSellList
extends L2GameClientPacket {
    private int _count;
    private boolean adj;
    private int[] abp;
    private int[] acl;
    private int[] adk;

    @Override
    protected void readImpl() {
        this.adj = this.readD() == 1;
        this._count = this.readD();
        if (this._count * 12 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        this.acl = new int[this._count];
        this.adk = new int[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
            this.acl[i] = this.readD();
            this.adk[i] = this.readD();
            if (this.acl[i] >= 1 && this.adk[i] >= 0 && ArrayUtils.indexOf(this.abp, this.abp[i]) >= i) continue;
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
        if (player.isMoving()) {
            player.stopMove();
        }
        if (!TradeHelper.checksIfCanOpenStore(player, this.adj ? 8 : 1)) {
            player.sendActionFailed();
            return;
        }
        CopyOnWriteArrayList<TradeItem> copyOnWriteArrayList = new CopyOnWriteArrayList<TradeItem>();
        int n = 0;
        player.getInventory().writeLock();
        try {
            for (int i = 0; i < this._count; ++i) {
                int n2 = this.abp[i];
                int n3 = this.acl[i];
                int n4 = this.adk[i];
                ItemInstance itemInstance = player.getInventory().getItemByObjectId(n2);
                if (itemInstance == null || itemInstance.getCount() < (long)n3 || !itemInstance.canBeTraded(player) || itemInstance.getItemId() == 57) continue;
                TradeItem tradeItem = new TradeItem(itemInstance);
                tradeItem.setCount(n3);
                tradeItem.setOwnersPrice(n4);
                n = SafeMath.addAndCheck(n, SafeMath.mulAndCheck(n3, n4));
                copyOnWriteArrayList.add(tradeItem);
            }
        }
        catch (ArithmeticException arithmeticException) {
            copyOnWriteArrayList.clear();
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        finally {
            player.getInventory().writeUnlock();
        }
        if (copyOnWriteArrayList.size() > player.getTradeLimit() || SafeMath.addAndCheck((long)n, player.getAdena()) >= Integer.MAX_VALUE) {
            player.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED, new PrivateStoreManageListSell(player, this.adj));
            return;
        }
        player.getInventory().writeLock();
        try {
            if (!copyOnWriteArrayList.isEmpty()) {
                player.setSellList(this.adj, copyOnWriteArrayList);
                player.setPrivateStoreType(this.adj ? 8 : 1);
                player.broadcastPacket(new PrivateStoreMsgSell(player));
                player.saveTradeList();
            } else {
                player.setSellList(false, Collections.emptyList());
                player.setPrivateStoreType(0);
            }
        }
        finally {
            player.getInventory().writeUnlock();
        }
        player.sendActionFailed();
    }
}

