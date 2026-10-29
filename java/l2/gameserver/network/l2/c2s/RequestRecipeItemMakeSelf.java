/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.RecipeItemMakeInfo;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.EtcItemTemplate;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.tuple.Pair;

public class RequestRecipeItemMakeSelf
extends L2GameClientPacket {
    private int XE;

    @Override
    protected void readImpl() {
        this.XE = this.readD();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInStoreMode()) {
            player.sendActionFailed();
            return;
        }
        if (player.isProcessingRequest()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }
        if (player.isInDuel()) {
            player.sendActionFailed();
            return;
        }
        Recipe recipe = RecipeHolder.getInstance().getRecipeById(this.XE);
        if (recipe == null || recipe.getMaterials().isEmpty() || recipe.getProducts().isEmpty()) {
            player.sendPacket((IStaticPacket)Msg.THE_RECIPE_IS_INCORRECT);
            return;
        }
        if (player.getCurrentMp() < (double)recipe.getMpConsume()) {
            player.sendPacket(Msg.NOT_ENOUGH_MP, new RecipeItemMakeInfo(player, recipe, 0));
            return;
        }
        if (!player.findRecipe(this.XE)) {
            player.sendPacket(Msg.PLEASE_REGISTER_A_RECIPE, ActionFail.STATIC);
            return;
        }
        boolean bl = false;
        List<Pair<ItemTemplate, Long>> list = recipe.getMaterials();
        List<Pair<ItemTemplate, Long>> list2 = Util.calcProducts(recipe.getProducts());
        player.getInventory().writeLock();
        try {
            Object object;
            for (Pair<ItemTemplate, Long> pair : list) {
                ItemTemplate itemTemplate = pair.getKey();
                long l = pair.getValue();
                if (l <= 0L) continue;
                if (Config.ALT_GAME_UNREGISTER_RECIPE && itemTemplate.getItemType() == EtcItemTemplate.EtcItemType.RECIPE) {
                    object = RecipeHolder.getInstance().getRecipeByItem(itemTemplate);
                    if (player.hasRecipe((Recipe)object)) continue;
                    player.sendPacket(Msg.NOT_ENOUGH_MATERIALS, new RecipeItemMakeInfo(player, recipe, 0));
                    return;
                }
                object = player.getInventory().getItemByItemId(itemTemplate.getItemId());
                if (object != null && ((ItemInstance)object).getCount() >= l) continue;
                player.sendPacket(Msg.NOT_ENOUGH_MATERIALS, new RecipeItemMakeInfo(player, recipe, 0));
                return;
            }
            int n = 0;
            long l = 0L;
            for (Pair<ItemTemplate, Long> pair : list2) {
                n = (int)((long)n + (long)pair.getKey().getWeight() * pair.getValue());
                l += pair.getKey().isStackable() ? 1L : pair.getValue();
            }
            if (!player.getInventory().validateWeight(n) || !player.getInventory().validateCapacity(l)) {
                player.sendPacket(Msg.WEIGHT_AND_VOLUME_LIMIT_HAS_BEEN_EXCEEDED_THAT_SKILL_IS_CURRENTLY_UNAVAILABLE, new RecipeItemMakeInfo(player, recipe, 0));
                return;
            }
            for (Pair<ItemTemplate, Long> pair : list) {
                object = pair.getKey();
                long l2 = pair.getValue();
                if (l2 <= 0L) continue;
                if (Config.ALT_GAME_UNREGISTER_RECIPE && ((ItemTemplate)object).getItemType() == EtcItemTemplate.EtcItemType.RECIPE) {
                    player.unregisterRecipe(RecipeHolder.getInstance().getRecipeByItem((ItemTemplate)object).getId());
                    continue;
                }
                if (!player.getInventory().destroyItemByItemId(((ItemTemplate)object).getItemId(), l2)) continue;
                player.sendPacket((IStaticPacket)SystemMessage2.removeItems(((ItemTemplate)object).getItemId(), l2));
            }
        }
        finally {
            player.getInventory().writeUnlock();
        }
        player.resetWaitSitTime();
        player.reduceCurrentMp(recipe.getMpConsume(), null);
        if (Rnd.chance(recipe.getSuccessRate())) {
            for (Pair<ItemTemplate, Long> pair : list2) {
                int n = pair.getKey().getItemId();
                long l = pair.getValue();
                ItemFunctions.addItem((Playable)player, n, l, true);
            }
            bl = true;
        }
        if (!bl) {
            for (Pair<ItemTemplate, Long> pair : list2) {
                player.sendPacket((IStaticPacket)new SystemMessage(960).addItemName(pair.getKey().getItemId()));
            }
        }
        player.sendPacket((IStaticPacket)new RecipeItemMakeInfo(player, recipe, bl ? 1 : 0));
    }
}

