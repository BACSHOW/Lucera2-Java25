/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.SoulCrystal;

public final class SoulCrystalHolder
extends AbstractHolder {
    private static final SoulCrystalHolder wU = new SoulCrystalHolder();
    private final TIntObjectHashMap<SoulCrystal> wV = new TIntObjectHashMap();

    public static SoulCrystalHolder getInstance() {
        return wU;
    }

    public void addCrystal(SoulCrystal soulCrystal) {
        this.wV.put(soulCrystal.getItemId(), soulCrystal);
    }

    public SoulCrystal getCrystal(int n) {
        return this.wV.get(n);
    }

    public SoulCrystal[] getCrystals() {
        return this.wV.getValues(new SoulCrystal[this.wV.size()]);
    }

    @Override
    public int size() {
        return this.wV.size();
    }

    @Override
    public void clear() {
        this.wV.clear();
    }
}

