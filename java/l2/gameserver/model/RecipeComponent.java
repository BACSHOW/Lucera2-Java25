/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

public class RecipeComponent {
    private final int _itemId;
    private final int KY;

    public RecipeComponent(int n, int n2) {
        this._itemId = n;
        this.KY = n2;
    }

    public int getItemId() {
        return this._itemId;
    }

    public int getQuantity() {
        return this.KY;
    }
}

