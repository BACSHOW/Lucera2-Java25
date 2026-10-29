/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemAttributes;

public class MultiSellIngredient
implements Cloneable {
    private int _itemId;
    private long Ls;
    private int OF;
    private ItemAttributes OG;
    private boolean OH;

    public MultiSellIngredient(int n, long l) {
        this(n, l, 0);
    }

    public MultiSellIngredient(int n, long l, int n2) {
        this._itemId = n;
        this.Ls = l;
        this.OF = n2;
        this.OH = false;
        this.OG = new ItemAttributes();
    }

    public MultiSellIngredient clone() {
        MultiSellIngredient multiSellIngredient = new MultiSellIngredient(this._itemId, this.Ls, this.OF);
        multiSellIngredient.setMantainIngredient(this.OH);
        multiSellIngredient.setItemAttributes(this.OG.clone());
        return multiSellIngredient;
    }

    public void setItemId(int n) {
        this._itemId = n;
    }

    public int getItemId() {
        return this._itemId;
    }

    public void setItemCount(long l) {
        this.Ls = l;
    }

    public long getItemCount() {
        return this.Ls;
    }

    public boolean isStackable() {
        return this._itemId <= 0 || ItemHolder.getInstance().getTemplate(this._itemId).isStackable();
    }

    public void setItemEnchant(int n) {
        this.OF = n;
    }

    public int getItemEnchant() {
        return this.OF;
    }

    public ItemAttributes getItemAttributes() {
        return this.OG;
    }

    public void setItemAttributes(ItemAttributes itemAttributes) {
        this.OG = itemAttributes;
    }

    public int hashCode() {
        int n = 1;
        n = 31 * n + (int)(this.Ls ^ this.Ls >>> 32);
        for (Element element : Element.VALUES) {
            n = 31 * n + this.OG.getValue(element);
        }
        n = 31 * n + this.OF;
        n = 31 * n + this._itemId;
        return n;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        MultiSellIngredient multiSellIngredient = (MultiSellIngredient)object;
        if (this._itemId != multiSellIngredient._itemId) {
            return false;
        }
        if (this.Ls != multiSellIngredient.Ls) {
            return false;
        }
        if (this.OF != multiSellIngredient.OF) {
            return false;
        }
        for (Element element : Element.VALUES) {
            if (this.OG.getValue(element) == multiSellIngredient.OG.getValue(element)) continue;
            return false;
        }
        return true;
    }

    public boolean getMantainIngredient() {
        return this.OH;
    }

    public void setMantainIngredient(boolean bl) {
        this.OH = bl;
    }
}

