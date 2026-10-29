/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.npc;

import gnu.trove.TIntHashSet;

public class AbsorbInfo {
    private final boolean avN;
    private final AbsorbType avO;
    private final int cJ;
    private final int avP;
    private final TIntHashSet avQ;

    public AbsorbInfo(boolean bl, AbsorbType absorbType, int n, int n2, int n3, int n4) {
        this.avN = bl;
        this.avO = absorbType;
        this.cJ = n;
        this.avP = n2;
        this.avQ = new TIntHashSet(n4 - n3);
        for (int i = n3; i <= n4; ++i) {
            this.avQ.add(i);
        }
    }

    public boolean isSkill() {
        return this.avN;
    }

    public AbsorbType getAbsorbType() {
        return this.avO;
    }

    public int getChance() {
        return this.cJ;
    }

    public int getCursedChance() {
        return this.avP;
    }

    public boolean canAbsorb(int n) {
        return this.avQ.contains(n);
    }

    public static enum AbsorbType {
        LAST_HIT,
        PARTY_ONE,
        PARTY_ALL,
        PARTY_RANDOM;

    }
}

