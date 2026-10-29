/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.ArrayList;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Player;
import l2.gameserver.templates.Henna;

public final class HennaHolder
extends AbstractHolder {
    private static final HennaHolder wc = new HennaHolder();
    private TIntObjectHashMap<Henna> wd = new TIntObjectHashMap();

    public static HennaHolder getInstance() {
        return wc;
    }

    public void addHenna(Henna henna) {
        this.wd.put(henna.getSymbolId(), henna);
    }

    public Henna getHenna(int n) {
        return this.wd.get(n);
    }

    public List<Henna> generateList(Player player) {
        ArrayList<Henna> arrayList = new ArrayList<Henna>();
        TIntObjectIterator<Henna> tIntObjectIterator = this.wd.iterator();
        while (tIntObjectIterator.hasNext()) {
            tIntObjectIterator.advance();
            Henna henna = tIntObjectIterator.value();
            if (!henna.isForThisClass(player)) continue;
            arrayList.add(henna);
        }
        return arrayList;
    }

    @Override
    public int size() {
        return this.wd.size();
    }

    @Override
    public void clear() {
        this.wd.clear();
    }
}

