/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.templates.item.ItemTemplate;

public class ProductItemComponent {
    private final int _itemId;
    private final int _count;
    private final int KF;
    private final boolean KG;

    public ProductItemComponent(int n, int n2) {
        this._itemId = n;
        this._count = n2;
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
        if (itemTemplate != null) {
            this.KF = itemTemplate.getWeight();
            this.KG = itemTemplate.isDropable();
        } else {
            this.KF = 0;
            this.KG = true;
        }
    }

    public int getItemId() {
        return this._itemId;
    }

    public int getCount() {
        return this._count;
    }

    public int getWeight() {
        return this.KF;
    }

    public boolean isDropable() {
        return this.KG;
    }
}

