/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item.support;

import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.support.EnchantTargetType;

public abstract class EnchantItem {
    private final int _itemId;
    private final double auP;
    private final ItemTemplate.Grade auQ;
    private final int he;
    private final int hf;
    private final EnchantTargetType auR;

    public EnchantItem(int n, double d, ItemTemplate.Grade grade, int n2, int n3, EnchantTargetType enchantTargetType) {
        this._itemId = n;
        this.auP = d;
        this.auQ = grade;
        this.he = n2;
        this.hf = n3;
        this.auR = enchantTargetType;
    }

    public int getItemId() {
        return this._itemId;
    }

    public double getChanceMod() {
        return this.auP;
    }

    public ItemTemplate.Grade getGrade() {
        return this.auQ;
    }

    public int getMinLvl() {
        return this.he;
    }

    public int getMaxLvl() {
        return this.hf;
    }

    public EnchantTargetType getTargetType() {
        return this.auR;
    }
}

