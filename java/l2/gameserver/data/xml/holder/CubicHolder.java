/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.CubicTemplate;

public final class CubicHolder
extends AbstractHolder {
    private static CubicHolder vO = new CubicHolder();
    private final TIntObjectHashMap<CubicTemplate> vP = new TIntObjectHashMap(10);

    public static CubicHolder getInstance() {
        return vO;
    }

    private CubicHolder() {
    }

    public void addCubicTemplate(CubicTemplate cubicTemplate) {
        this.vP.put(this.hash(cubicTemplate.getId(), cubicTemplate.getLevel()), cubicTemplate);
    }

    public CubicTemplate getTemplate(int n, int n2) {
        return this.vP.get(this.hash(n, n2));
    }

    public int hash(int n, int n2) {
        return n * 10000 + n2;
    }

    @Override
    public int size() {
        return this.vP.size();
    }

    @Override
    public void clear() {
        this.vP.clear();
    }
}

