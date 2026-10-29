/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import java.util.Collection;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.RecipeBookItemList;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class Recipes
extends ScriptItemHandler {
    private static int[] iv = null;

    public Recipes() {
        Collection<Recipe> collection = RecipeHolder.getInstance().getRecipes();
        iv = new int[collection.size()];
        int n = 0;
        for (Recipe recipe : collection) {
            Recipes.iv[n++] = recipe.getItem().getItemId();
        }
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        Recipe recipe = RecipeHolder.getInstance().getRecipeByItem(itemInstance);
        switch (recipe.getType()) {
            case ERT_DWARF: {
                if (player.getDwarvenRecipeLimit() > 0) {
                    if (player.getDwarvenRecipeBook().size() >= player.getDwarvenRecipeLimit()) {
                        player.sendPacket((IStaticPacket)Msg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
                        return false;
                    }
                    if (recipe.getRequiredSkillLvl() > player.getSkillLevel(172)) {
                        player.sendPacket((IStaticPacket)Msg.CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
                        return false;
                    }
                    if (player.hasRecipe(recipe)) {
                        player.sendPacket((IStaticPacket)Msg.THAT_RECIPE_IS_ALREADY_REGISTERED);
                        return false;
                    }
                    if (!player.getInventory().destroyItem(itemInstance, 1L)) {
                        player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                        return false;
                    }
                    player.registerRecipe(recipe, true);
                    player.sendPacket((IStaticPacket)new SystemMessage(851).addItemName(itemInstance.getItemId()));
                    player.sendPacket((IStaticPacket)new RecipeBookItemList(player, true));
                    return true;
                }
                player.sendPacket((IStaticPacket)Msg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
                break;
            }
            case ERT_COMMON: {
                if (player.getCommonRecipeLimit() > 0) {
                    if (player.getCommonRecipeBook().size() >= player.getCommonRecipeLimit()) {
                        player.sendPacket((IStaticPacket)Msg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
                        return false;
                    }
                    if (player.hasRecipe(recipe)) {
                        player.sendPacket((IStaticPacket)Msg.THAT_RECIPE_IS_ALREADY_REGISTERED);
                        return false;
                    }
                    if (!player.getInventory().destroyItem(itemInstance, 1L)) {
                        player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                        return false;
                    }
                    player.registerRecipe(recipe, true);
                    player.sendPacket((IStaticPacket)new SystemMessage(851).addItemName(itemInstance.getItemId()));
                    player.sendPacket((IStaticPacket)new RecipeBookItemList(player, false));
                    return true;
                }
                player.sendPacket((IStaticPacket)Msg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
            }
        }
        return false;
    }

    @Override
    public int[] getItemIds() {
        return iv;
    }
}

