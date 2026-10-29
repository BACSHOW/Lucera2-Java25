/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.npc;

public class MinionData {
    private final int avS;
    private final int avT;

    public MinionData(int n, int n2) {
        this.avS = n;
        this.avT = n2;
    }

    public int getMinionId() {
        return this.avS;
    }

    public int getAmount() {
        return this.avT;
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
        return ((MinionData)object).getMinionId() == this.getMinionId();
    }
}

