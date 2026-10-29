/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import l2.commons.collections.MultiValueSet;

public class StatsSet
extends MultiValueSet<String> {
    private static final long serialVersionUID = -2209589233655930756L;
    public static final StatsSet EMPTY = new StatsSet(){

        @Override
        public Object put(String string, Object object) {
            throw new UnsupportedOperationException();
        }
    };

    public StatsSet() {
    }

    public StatsSet(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public StatsSet clone() {
        return new StatsSet(this);
    }
}

