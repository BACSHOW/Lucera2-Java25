/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item.support;

public class VariationGroupData {
    private final long avC;
    private final int avz;
    private final int ahx;
    private final long avD;

    public VariationGroupData(int n, int n2, long l, long l2) {
        this.avz = n;
        this.ahx = n2;
        this.avD = l;
        this.avC = l2;
    }

    public long getCancelPrice() {
        return this.avC;
    }

    public int getMineralItemId() {
        return this.avz;
    }

    public int getGemstoneItemId() {
        return this.ahx;
    }

    public long getGemstoneItemCnt() {
        return this.avD;
    }
}

