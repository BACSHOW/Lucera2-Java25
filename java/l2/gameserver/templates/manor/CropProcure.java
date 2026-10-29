/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.manor;

public class CropProcure {
    int _rewardType;
    int _cropId;
    long _buyResidual;
    long _buy;
    long _price;

    public CropProcure(int n) {
        this._cropId = n;
        this._buyResidual = 0L;
        this._rewardType = 0;
        this._buy = 0L;
        this._price = 0L;
    }

    public CropProcure(int n, long l, int n2, long l2, long l3) {
        this._cropId = n;
        this._buyResidual = l;
        this._rewardType = n2;
        this._buy = l2;
        this._price = l3;
        if (this._price < 0L) {
            this._price = 0L;
        }
    }

    public int getReward() {
        return this._rewardType;
    }

    public int getId() {
        return this._cropId;
    }

    public long getAmount() {
        return this._buyResidual;
    }

    public long getStartAmount() {
        return this._buy;
    }

    public long getPrice() {
        return this._price;
    }

    public void setAmount(long l) {
        this._buyResidual = l;
    }
}

