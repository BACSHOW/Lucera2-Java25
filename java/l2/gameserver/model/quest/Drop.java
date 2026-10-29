/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.quest;

import org.apache.commons.lang3.ArrayUtils;

public class Drop {
    public final int condition;
    public final int maxcount;
    public final int chance;
    public int[] itemList = ArrayUtils.EMPTY_INT_ARRAY;

    public Drop(int n, int n2, int n3) {
        this.condition = n;
        this.maxcount = n2;
        this.chance = n3;
    }

    public Drop addItem(int n) {
        this.itemList = ArrayUtils.add(this.itemList, n);
        return this;
    }
}

