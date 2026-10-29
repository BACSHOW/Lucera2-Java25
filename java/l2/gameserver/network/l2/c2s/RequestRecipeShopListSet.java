/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.RecipeShopMsg;
import l2.gameserver.utils.TradeHelper;

public class RequestRecipeShopListSet
extends L2GameClientPacket {
    private int[] acF;
    private long[] acG;
    private int _count;

    @Override
    protected void readImpl() {
        this._count = this.readD();
        if (this._count * 8 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.acF = new int[this._count];
        this.acG = new long[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.acF[i] = this.readD();
            this.acG[i] = this.readD();
            if (this.acG[i] >= 0L) continue;
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
        if (!TradeHelper.checksIfCanOpenStore(player, 5)) {
            player.sendActionFailed();
            return;
        }
        if (this._count > Config.MAX_PVTCRAFT_SLOTS) {
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        CopyOnWriteArrayList<ManufactureItem> copyOnWriteArrayList = new CopyOnWriteArrayList<ManufactureItem>();
        for (int i = 0; i < this._count; ++i) {
            int n = this.acF[i];
            long l = this.acG[i];
            if (!player.findRecipe(n)) continue;
            ManufactureItem manufactureItem = new ManufactureItem(n, l);
            copyOnWriteArrayList.add(manufactureItem);
        }
        player.getInventory().writeLock();
        try {
            if (!copyOnWriteArrayList.isEmpty()) {
                player.setCreateList(copyOnWriteArrayList);
                player.setPrivateStoreType(5);
                player.broadcastPacket(new RecipeShopMsg(player));
                player.saveTradeList();
            } else {
                player.setCreateList(Collections.emptyList());
                player.setPrivateStoreType(0);
            }
        }
        finally {
            player.getInventory().writeUnlock();
        }
        player.sendActionFailed();
    }
}

