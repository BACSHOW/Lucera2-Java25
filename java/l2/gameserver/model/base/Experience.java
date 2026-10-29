/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

import l2.gameserver.Config;

public class Experience {
    public static final long[] LEVEL = Config.EXPERIENCE;

    public static double penaltyModifier(long l, double d) {
        return Math.max(1.0 - (double)l * d / 100.0, 0.0);
    }

    public static int getMaxLevel() {
        return Config.ALT_MAX_LEVEL;
    }

    public static int getMaxSubLevel() {
        return Config.ALT_MAX_SUB_LEVEL;
    }

    public static int getLevel(long l) {
        int n = 0;
        for (int i = 0; i < LEVEL.length; ++i) {
            long l2 = LEVEL[i];
            if (l < l2) continue;
            n = i;
        }
        return n;
    }

    public static long getExpForLevel(int n) {
        if (n >= LEVEL.length) {
            return 0L;
        }
        return LEVEL[n];
    }

    public static double getExpPercent(int n, long l) {
        return (double)(l - Experience.getExpForLevel(n)) / ((double)(Experience.getExpForLevel(n + 1) - Experience.getExpForLevel(n)) / 100.0) * 0.01;
    }
}

