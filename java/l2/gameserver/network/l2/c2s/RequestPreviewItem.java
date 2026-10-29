/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.HashMap;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.ShopPreviewInfo;
import l2.gameserver.network.l2.s2c.ShopPreviewList;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestPreviewItem
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestPreviewItem.class);
    private int acw;
    private int wr;
    private int _count;
    private int[] abp;

    @Override
    protected void readImpl() {
        this.acw = this.readD();
        this.wr = this.readD();
        this._count = this.readD();
        if (this._count * 4 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.abp = new int[this._count];
        for (int i = 0; i < this._count; ++i) {
            this.abp[i] = this.readD();
        }
    }

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
        boolean bl3 = false;
        long l = 0L;
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        try {
            for (int i = 0; i < this._count; ++i) {
                int n;
                int n2 = this.abp[i];
                if (npcTradeList.getItemByItemId(n2) == null) {
                    player.sendActionFailed();
                    return;
                }
                ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n2);
                if (itemTemplate == null || !itemTemplate.isEquipable() || (n = Inventory.getPaperdollIndex(itemTemplate.getBodyPart())) < 0) continue;
                if (hashMap.containsKey(n)) {
                    player.sendPacket((IStaticPacket)Msg.THOSE_ITEMS_MAY_NOT_BE_TRIED_ON_SIMULTANEOUSLY);
                    return;
                }
                hashMap.put(n, n2);
                l += (long)ShopPreviewList.getWearPrice(itemTemplate);
            }
            if (!player.reduceAdena(l)) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
        }
        catch (ArithmeticException arithmeticException) {
            this.sendPacket((L2GameServerPacket)Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }
        if (!hashMap.isEmpty()) {
            player.sendPacket((IStaticPacket)new ShopPreviewInfo(hashMap));
            ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(player), Config.WEAR_DELAY * 1000);
        }
    }

    private static class RemoveWearItemsTask
    extends RunnableImpl {
        private Player zT;

        public RemoveWearItemsTask(Player player) {
            this.zT = player;
        }

        @Override
        public void runImpl() throws Exception {
            this.zT.sendPacket((IStaticPacket)Msg.TRYING_ON_MODE_HAS_ENDED);
            this.zT.sendUserInfo(true);
        }
    }
}

