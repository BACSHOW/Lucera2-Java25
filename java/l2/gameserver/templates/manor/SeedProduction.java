/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.manor;

public class SeedProduction {
    int _seedId;
    long _residual;
    long _price;
    long _sales;

    public SeedProduction(int n) {
        this._seedId = n;
        this._sales = 0L;
        this._price = 0L;
        this._sales = 0L;
    }

    public SeedProduction(int n, long l, long l2, long l3) {
        this._seedId = n;
        this._residual = l;
        this._price = l2;
        this._sales = l3;
    }

    public int getId() {
        return this._seedId;
    }

    public long getCanProduce() {
        return this._residual;
    }

    public long getPrice() {
        return this._price;
    }

    public long getStartProduce() {
        return this._sales;
    }

    public void setCanProduce(long l) {
        this._residual = l;
    }
}

