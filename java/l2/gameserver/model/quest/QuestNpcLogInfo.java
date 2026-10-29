/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.quest;

public class QuestNpcLogInfo {
    private final int[] hd;
    private final String YX;
    private final int YY;

    public QuestNpcLogInfo(int[] nArray, String string, int n) {
        this.hd = nArray;
        this.YX = string;
        this.YY = n;
    }

    public int[] getNpcIds() {
        return this.hd;
    }

    public String getVarName() {
        return this.YX;
    }

    public int getMaxCount() {
        return this.YY;
    }
}

