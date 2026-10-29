/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly;

public enum CompetitionState {
    INIT(0),
    STAND_BY(1),
    PLAYING(2),
    FINISH(0);

    private int RC;

    private CompetitionState(int n2) {
        this.RC = n2;
    }

    public int getStateId() {
        return this.RC;
    }
}

