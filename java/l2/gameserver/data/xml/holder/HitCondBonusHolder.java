/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.base.HitCondBonusType;

public final class HitCondBonusHolder
extends AbstractHolder {
    private static final HitCondBonusHolder we = new HitCondBonusHolder();
    private final Map<Integer, Double> wf = new HashMap<Integer, Double>();

    public static HitCondBonusHolder getInstance() {
        return we;
    }

    public void addHitCondBonus(HitCondBonusType hitCondBonusType, double d) {
        this.wf.put(hitCondBonusType.ordinal(), d);
    }

    public double getHitCondBonus(HitCondBonusType hitCondBonusType) {
        return this.wf.get(hitCondBonusType.ordinal());
    }

    @Override
    public int size() {
        return this.wf.size();
    }

    @Override
    public void clear() {
        this.wf.clear();
    }
}

