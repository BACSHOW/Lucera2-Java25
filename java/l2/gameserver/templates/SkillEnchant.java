/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

public class SkillEnchant {
    private final int _skillId;
    private final int _skillLevel;
    private final int aqn;
    private final int atE;
    private final long _exp;
    private final int _sp;
    private final int[] atF;
    private final int _itemId;
    private final long Ls;

    public SkillEnchant(int n, int n2, int n3, int n4, long l, int n5, int[] nArray, int n6, long l2) {
        this._skillId = n;
        this._skillLevel = n2;
        this.aqn = n3;
        this.atE = n4;
        this._exp = l;
        this._sp = n5;
        this.atF = nArray;
        this._itemId = n6;
        this.Ls = l2;
    }

    public int[] getChances() {
        return this.atF;
    }

    public long getExp() {
        return this._exp;
    }

    public long getItemCount() {
        return this.Ls;
    }

    public int getItemId() {
        return this._itemId;
    }

    public int getEnchantLevel() {
        return this.aqn;
    }

    public int getRouteId() {
        return this.atE;
    }

    public int getSkillId() {
        return this._skillId;
    }

    public int getSkillLevel() {
        return this._skillLevel;
    }

    public int getSp() {
        return this._sp;
    }
}

