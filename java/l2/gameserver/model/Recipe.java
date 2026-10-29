/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.tuple.Pair;

public class Recipe {
    private final int _id;
    private final ItemTemplate KP;
    private final ERecipeType KQ;
    private final int KR;
    private final int KS;
    private final int KT;
    private final List<Pair<ItemTemplate, Long>> KU;
    private final List<Pair<ItemTemplate, Pair<Long, Double>>> KV;
    private final List<Pair<ItemTemplate, Long>> KW;

    public Recipe(int n, ItemTemplate itemTemplate, ERecipeType eRecipeType, int n2, int n3, int n4, List<Pair<ItemTemplate, Long>> list, List<Pair<ItemTemplate, Pair<Long, Double>>> list2, List<Pair<ItemTemplate, Long>> list3) {
        this._id = n;
        this.KP = itemTemplate;
        this.KQ = eRecipeType;
        this.KR = n2;
        this.KS = n3;
        this.KT = n4;
        this.KU = list;
        this.KV = list2;
        this.KW = list3;
    }

    public int getId() {
        return this._id;
    }

    public ItemTemplate getItem() {
        return this.KP;
    }

    public ERecipeType getType() {
        return this.KQ;
    }

    public int getRequiredSkillLvl() {
        return this.KR;
    }

    public int getMpConsume() {
        return this.KS;
    }

    public int getSuccessRate() {
        return this.KT;
    }

    public List<Pair<ItemTemplate, Long>> getMaterials() {
        return this.KU;
    }

    public List<Pair<ItemTemplate, Pair<Long, Double>>> getProducts() {
        return this.KV;
    }

    public List<Pair<ItemTemplate, Long>> getNpcFees() {
        return this.KW;
    }

    public int hashCode() {
        return this._id;
    }

    public String toString() {
        return "Recipe{_id=" + this._id + ", _item=" + this.KP + ", _type=" + (Object)((Object)this.KQ) + ", _requiredSkillLvl=" + this.KR + ", _mpConsume=" + this.KS + ", _successRate=" + this.KT + ", _materials=" + this.KU + ", _products=" + this.KV + ", _npcFees=" + this.KW + '}';
    }

    public static enum ERecipeType {
        ERT_DWARF,
        ERT_COMMON;


        public boolean isApplicableBy(Player player) {
            return this != ERT_DWARF || player.getRace() == Race.dwarf;
        }
    }
}

