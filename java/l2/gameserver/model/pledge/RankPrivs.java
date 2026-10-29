/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.pledge;

public class RankPrivs {
    private int YM;
    private int _party;
    private int YN;

    public RankPrivs(int n, int n2, int n3) {
        this.YM = n;
        this._party = n2;
        this.YN = n3;
    }

    public int getRank() {
        return this.YM;
    }

    public int getParty() {
        return this._party;
    }

    public void setParty(int n) {
        this._party = n;
    }

    public int getPrivs() {
        return this.YN;
    }

    public void setPrivs(int n) {
        this.YN = n;
    }
}

