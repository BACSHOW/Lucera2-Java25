/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.npc;

import gnu.trove.TIntArrayList;
import l2.commons.util.TroveUtils;

public class Faction {
    public static final String none = "none";
    public static final Faction NONE = new Faction("none");
    public final String factionId;
    public int factionRange;
    public TIntArrayList ignoreId = TroveUtils.EMPTY_INT_ARRAY_LIST;

    public Faction(String string) {
        this.factionId = string;
    }

    public String getName() {
        return this.factionId;
    }

    public void setRange(int n) {
        this.factionRange = n;
    }

    public int getRange() {
        return this.factionRange;
    }

    public void addIgnoreNpcId(int n) {
        if (this.ignoreId.isEmpty()) {
            this.ignoreId = new TIntArrayList();
        }
        this.ignoreId.add(n);
    }

    public boolean isIgnoreNpcId(int n) {
        return this.ignoreId.contains(n);
    }

    public boolean isNone() {
        return this.factionId.isEmpty() || this.factionId.equals(none);
    }

    public boolean equals(Faction faction) {
        return !this.isNone() && faction.getName().equalsIgnoreCase(this.factionId);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        return this.equals((Faction)object);
    }

    public String toString() {
        return this.isNone() ? none : this.factionId;
    }
}

