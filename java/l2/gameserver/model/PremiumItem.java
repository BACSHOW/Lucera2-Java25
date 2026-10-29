/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

public class PremiumItem {
    private int _itemId;
    private long CE;
    private String Kv;

    public PremiumItem(int n, long l, String string) {
        this._itemId = n;
        this.CE = l;
        this.Kv = string;
    }

    public void updateCount(long l) {
        this.CE = l;
    }

    public int getItemId() {
        return this._itemId;
    }

    public long getCount() {
        return this.CE;
    }

    public String getSender() {
        return this.Kv;
    }
}

