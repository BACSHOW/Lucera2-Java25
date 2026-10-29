/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RecipeHolder
extends AbstractHolder {
    private static final Logger LOG = LoggerFactory.getLogger(RecipeHolder.class);
    private static final RecipeHolder wH = new RecipeHolder();
    private final Map<Integer, Recipe> wI = new HashMap<Integer, Recipe>();
    private final Map<Integer, Recipe> wJ = new HashMap<Integer, Recipe>();

    public static final RecipeHolder getInstance() {
        return wH;
    }

    public void addRecipe(Recipe recipe) {
        if (this.wI.containsKey(recipe.getId())) {
            LOG.warn("Recipe \"" + recipe.getId() + "\" already exists.");
        }
        this.wI.put(recipe.getId(), recipe);
        this.wJ.put(recipe.getItem().getItemId(), recipe);
    }

    public Recipe getRecipeById(int n) {
        return this.wI.get(n);
    }

    public Recipe getRecipeByItem(ItemTemplate itemTemplate) {
        return this.getRecipeByItem(itemTemplate.getItemId());
    }

    public Recipe getRecipeByItem(ItemInstance itemInstance) {
        return this.getRecipeByItem(itemInstance.getItemId());
    }

    public Recipe getRecipeByItem(int n) {
        return this.wJ.get(n);
    }

    public Collection<Recipe> getRecipes() {
        return Collections.unmodifiableMap(this.wI).values();
    }

    @Override
    public int size() {
        return this.wI.size();
    }

    @Override
    public void clear() {
        this.wI.clear();
        this.wJ.clear();
    }
}

