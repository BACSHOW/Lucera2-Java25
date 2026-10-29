/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

public class ManufactureItem {
    private final int XE;
    private final long XF;

    public ManufactureItem(int n, long l) {
        this.XE = n;
        this.XF = l;
    }

    public int getRecipeId() {
        return this.XE;
    }

    public long getCost() {
        return this.XF;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        return ((ManufactureItem)object).getRecipeId() == this.getRecipeId();
    }
}

