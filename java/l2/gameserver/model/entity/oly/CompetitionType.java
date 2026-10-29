/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly;

public enum CompetitionType {
    TEAM_CLASS_FREE(0),
    CLASS_FREE(1),
    CLASS_INDIVIDUAL(2),
    CLASS_TYPE_INDIVIDUAL(4);

    private int RE;

    private CompetitionType(int n2) {
        this.RE = n2;
    }

    public int getTypeIdx() {
        return this.RE;
    }

    public static CompetitionType getTypeOf(int n) {
        for (CompetitionType competitionType : CompetitionType.values()) {
            if (competitionType.getTypeIdx() != n) continue;
            return competitionType;
        }
        return null;
    }
}

