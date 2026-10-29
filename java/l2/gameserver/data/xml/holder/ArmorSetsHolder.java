/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.ArmorSet;

public final class ArmorSetsHolder
extends AbstractHolder {
    private static final ArmorSetsHolder vw = new ArmorSetsHolder();
    private List<ArmorSet> vx = new ArrayList<ArmorSet>();

    public static ArmorSetsHolder getInstance() {
        return vw;
    }

    public void addArmorSet(ArmorSet armorSet) {
        this.vx.add(armorSet);
    }

    public ArmorSet getArmorSet(int n) {
        for (ArmorSet armorSet : this.vx) {
            if (!armorSet.getChestItemIds().contains(n)) continue;
            return armorSet;
        }
        return null;
    }

    @Override
    public int size() {
        return this.vx.size();
    }

    @Override
    public void clear() {
        this.vx.clear();
    }
}

