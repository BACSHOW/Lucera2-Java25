/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items;

import l2.gameserver.model.items.ItemInfo;
import l2.gameserver.model.items.ItemInstance;

public final class TradeItem
extends ItemInfo {
    private long _price;
    private long XK;
    private long XL;
    private long XM;
    private int XN;

    public TradeItem() {
    }

    public TradeItem(ItemInstance itemInstance) {
        super(itemInstance);
        this.setReferencePrice(itemInstance.getReferencePrice());
    }

    public void setOwnersPrice(long l) {
        this._price = l;
    }

    public long getOwnersPrice() {
        return this._price;
    }

    public void setReferencePrice(long l) {
        this.XK = l;
    }

    public long getReferencePrice() {
        return this.XK;
    }

    public long getStorePrice() {
        return this.getReferencePrice() / 2L;
    }

    public void setCurrentValue(long l) {
        this.XL = l;
    }

    public long getCurrentValue() {
        return this.XL;
    }

    public void setRechargeTime(int n) {
        this.XN = n;
    }

    public int getRechargeTime() {
        return this.XN;
    }

    public boolean isCountLimited() {
        return this.getCount() > 0L;
    }

    public long getNextRechargeTime() {
        return this.XM;
    }

    public void setNextRechargeTime(long l) {
        this.XM = l;
    }
}

