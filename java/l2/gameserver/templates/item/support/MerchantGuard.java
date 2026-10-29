/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item.support;

import l2.gameserver.model.entity.SevenSigns;
import org.napile.primitive.sets.IntSet;

public class MerchantGuard {
    private int _itemId;
    private int _npcId;
    private int avx;
    private IntSet avy;

    public MerchantGuard(int n, int n2, int n3, IntSet intSet) {
        this._itemId = n;
        this._npcId = n2;
        this.avx = n3;
        this.avy = intSet;
    }

    public int getItemId() {
        return this._itemId;
    }

    public int getNpcId() {
        return this._npcId;
    }

    public int getMax() {
        return this.avx;
    }

    public boolean isValidSSQPeriod() {
        return SevenSigns.getInstance().getCurrentPeriod() == 3 && this.avy.contains(SevenSigns.getInstance().getSealOwner(3));
    }
}

