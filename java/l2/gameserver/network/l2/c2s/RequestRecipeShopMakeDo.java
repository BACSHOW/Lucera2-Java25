/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.RecipeShopItemInfo;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.TradeHelper;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.tuple.Pair;

public class RequestRecipeShopMakeDo
extends L2GameClientPacket {
    private int _manufacturerId;
    private int XE;
    private long _price;

    @Override
    protected void readImpl() {
        this._manufacturerId = this.readD();
        this.XE = this.readD();
        this._price = this.readD();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        Object object;
        long n4;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
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
        if (!player.getPlayerAccess().UseTrade) {
            player.sendPacket((IStaticPacket)Msg.THIS_ACCOUNT_CANOT_USE_PRIVATE_STORES);
            return;
        }
        Player player2 = (Player)player.getVisibleObject(this._manufacturerId);
        if (player2 == null || player2.getPrivateStoreType() != 5 || !player2.isInActingRange(player)) {
            player.sendActionFailed();
            return;
        }
        Recipe recipe = null;
        for (ManufactureItem object22 : player2.getCreateList()) {
            if (object22.getRecipeId() != this.XE || this._price != object22.getCost()) continue;
            recipe = RecipeHolder.getInstance().getRecipeById(this.XE);
            break;
        }
        if (recipe == null) {
            player.sendActionFailed();
            return;
        }
        int n = 0;
        if (recipe.getProducts().isEmpty() || recipe.getMaterials().isEmpty()) {
            player2.sendMessage(new CustomMessage("l2p.gameserver.RecipeController.NoRecipe", player2, new Object[0]).addItemName(recipe.getItem()));
            player.sendMessage(new CustomMessage("l2p.gameserver.RecipeController.NoRecipe", player2, new Object[0]).addItemName(recipe.getItem()));
            return;
        }
        if (!player2.findRecipe(this.XE)) {
            player.sendActionFailed();
            return;
        }
        if (player2.getCurrentMp() < (double)recipe.getMpConsume()) {
            player2.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
            player.sendPacket(Msg.NOT_ENOUGH_MP, new RecipeShopItemInfo(player, player2, this.XE, this._price, n));
            return;
        }
        List<Pair<ItemTemplate, Long>> list = recipe.getMaterials();
        List<Pair<ItemTemplate, Long>> list2 = Util.calcProducts(recipe.getProducts());
        player.getInventory().writeLock();
        try {
            Object object2;
            if (player.getAdena() < this._price) {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfo(player, player2, this.XE, this._price, n));
                return;
            }
            for (Pair<ItemTemplate, Long> l3 : list) {
                ItemTemplate n3 = l3.getKey();
                long iterator2 = l3.getValue();
                if (iterator2 <= 0L) continue;
                object2 = player.getInventory().getItemByItemId(n3.getItemId());
                if (object2 != null && ((ItemInstance)object2).getCount() >= iterator2) continue;
                player.sendPacket(Msg.NOT_ENOUGH_MATERIALS, new RecipeShopItemInfo(player, player2, this.XE, this._price, n));
                return;
            }
            int object3 = 0;
            long pair = 0L;
            for (Pair<ItemTemplate, Long> l5 : list2) {
                object3 = (int)((long)object3 + (long)l5.getKey().getWeight() * l5.getValue());
                pair += l5.getKey().isStackable() ? 1L : l5.getValue();
            }
            if (!player.getInventory().validateWeight(object3) || !player.getInventory().validateCapacity(pair)) {
                player.sendPacket(Msg.THE_WEIGHT_AND_VOLUME_LIMIT_OF_INVENTORY_MUST_NOT_BE_EXCEEDED, new RecipeShopItemInfo(player, player2, this.XE, this._price, n));
                return;
            }
            if (!player.reduceAdena(this._price, false)) {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA, new RecipeShopItemInfo(player, player2, this.XE, this._price, n));
                return;
            }
            for (Pair<ItemTemplate, Long> l5 : list) {
                object2 = l5.getKey();
                long l = l5.getValue();
                if (l <= 0L) continue;
                player.getInventory().destroyItemByItemId(((ItemTemplate)object2).getItemId(), l);
                player.sendPacket((IStaticPacket)SystemMessage2.removeItems(((ItemTemplate)object2).getItemId(), l));
            }
            n4 = TradeHelper.getTax(player2, this._price);
            if (n4 > 0L) {
                this._price -= n4;
                player2.sendMessage(new CustomMessage("trade.HavePaidTax", player2, new Object[0]).addNumber(n4));
            }
            player2.addAdena(this._price);
        }
        finally {
            player.getInventory().writeUnlock();
        }
        for (Pair<ItemTemplate, Long> pair : list2) {
            player2.sendMessage(new CustomMessage("l2p.gameserver.RecipeController.GotOrder", player2, new Object[0]).addItemName(pair.getKey()));
        }
        player2.reduceCurrentMp(recipe.getMpConsume(), null);
        player2.sendStatusUpdate(false, false, 11);
        if (Rnd.chance(recipe.getSuccessRate())) {
            for (Pair<ItemTemplate, Long> pair : list2) {
                int pair2 = pair.getKey().getItemId();
                n4 = pair.getValue();
                ItemFunctions.addItem((Playable)player, pair2, n4, true);
            }
            n = 1;
        }
        if (n == 0) {
            for (Pair<ItemTemplate, Long> pair : list2) {
                int n5 = pair.getKey().getItemId();
                object = new SystemMessage(1150);
                ((SystemMessage)object).addString(player2.getName());
                ((SystemMessage)object).addItemName(n5);
                ((SystemMessage)object).addNumber(this._price);
                player.sendPacket((IStaticPacket)object);
                object = new SystemMessage(1149);
                ((SystemMessage)object).addString(player.getName());
                ((SystemMessage)object).addItemName(n5);
                ((SystemMessage)object).addNumber(this._price);
                player2.sendPacket((IStaticPacket)object);
            }
        } else {
            for (Pair<ItemTemplate, Long> pair : list2) {
                int n2 = pair.getKey().getItemId();
                long l = pair.getValue();
                if (l > 1L) {
                    object = new SystemMessage(1148);
                    ((SystemMessage)object).addString(player2.getName());
                    ((SystemMessage)object).addItemName(n2);
                    ((SystemMessage)object).addNumber(l);
                    ((SystemMessage)object).addNumber(this._price);
                    player.sendPacket((IStaticPacket)object);
                    object = new SystemMessage(1152);
                    ((SystemMessage)object).addString(player.getName());
                    ((SystemMessage)object).addItemName(n2);
                    ((SystemMessage)object).addNumber(l);
                    ((SystemMessage)object).addNumber(this._price);
                    player2.sendPacket((IStaticPacket)object);
                    continue;
                }
                object = new SystemMessage(1146);
                ((SystemMessage)object).addString(player2.getName());
                ((SystemMessage)object).addItemName(n2);
                ((SystemMessage)object).addNumber(this._price);
                player.sendPacket((IStaticPacket)object);
                object = new SystemMessage(1151);
                ((SystemMessage)object).addString(player.getName());
                ((SystemMessage)object).addItemName(n2);
                ((SystemMessage)object).addNumber(this._price);
                player2.sendPacket((IStaticPacket)object);
            }
        }
        player.sendChanges();
        player.sendPacket((IStaticPacket)new RecipeShopItemInfo(player, player2, this.XE, this._price, n));
    }
}

