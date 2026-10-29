/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

public class SoulCrystal {
    private final int _itemId;
    private final int _level;
    private final int atG;
    private final int atH;

    public SoulCrystal(int n, int n2, int n3, int n4) {
        this._itemId = n;
        this._level = n2;
        this.atG = n3;
        this.atH = n4;
    }

    public int getItemId() {
        return this._itemId;
    }

    public int getLevel() {
        return this._level;
    }

    public int getNextItemId() {
        return this.atG;
    }

    public int getCursedNextItemId() {
        return this.atH;
    }
}

