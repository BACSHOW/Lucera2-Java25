/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.item.support.VariationGroupData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class VariationGroupHolder
extends AbstractHolder {
    private static final VariationGroupHolder xc = new VariationGroupHolder();
    private List<Pair<int[], VariationGroupData>> xd = new ArrayList<Pair<int[], VariationGroupData>>();

    public static VariationGroupHolder getInstance() {
        return xc;
    }

    private VariationGroupHolder() {
    }

    @Override
    public int size() {
        return this.xd.size();
    }

    @Override
    public void clear() {
        this.xd.clear();
    }

    public void add(int[] nArray, VariationGroupData variationGroupData) {
        int[] nArray2 = (int[])nArray.clone();
        Arrays.sort(nArray2);
        this.xd.add(new ImmutablePair<int[], VariationGroupData>(nArray2, variationGroupData));
    }

    public void addSorted(int[] nArray, VariationGroupData variationGroupData) {
        this.xd.add(new ImmutablePair<int[], VariationGroupData>(nArray, variationGroupData));
    }

    public List<VariationGroupData> getDataForItemId(int n) {
        ArrayList<VariationGroupData> arrayList = new ArrayList<VariationGroupData>();
        for (Pair<int[], VariationGroupData> pair : this.xd) {
            int[] nArray = pair.getLeft();
            if (Arrays.binarySearch(nArray, n) < 0) continue;
            arrayList.add(pair.getRight());
        }
        return arrayList;
    }
}

