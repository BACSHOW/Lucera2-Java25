/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import java.util.concurrent.atomic.AtomicLong;
import l2.gameserver.instancemanager.ServerVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStats {
    private static final Logger _log = LoggerFactory.getLogger(GameStats.class);
    private static AtomicLong awE = new AtomicLong(0L);
    private static AtomicLong awF = new AtomicLong(0L);
    private static AtomicLong awG = new AtomicLong(0L);
    private static long awH;
    private static AtomicLong awI;
    private static long awJ;
    private static AtomicLong awK;

    public static void increaseUpdatePlayerBase() {
        awE.incrementAndGet();
    }

    public static long getUpdatePlayerBase() {
        return awE.get();
    }

    public static void incrementPlayerEnterGame() {
        awF.incrementAndGet();
    }

    public static long getPlayerEnterGame() {
        return awF.get();
    }

    public static void addTax(long l) {
        long l2 = awG.addAndGet(l);
        if (System.currentTimeMillis() - awH < 10000L) {
            return;
        }
        awH = System.currentTimeMillis();
        ServerVariables.set("taxsum", l2);
    }

    public static void addRoulette(long l) {
        long l2 = awI.addAndGet(l);
        if (System.currentTimeMillis() - awJ < 10000L) {
            return;
        }
        awJ = System.currentTimeMillis();
        ServerVariables.set("rouletteSum", l2);
    }

    public static long getTaxSum() {
        return awG.get();
    }

    public static long getRouletteSum() {
        return awI.get();
    }

    public static void addAdena(long l) {
        awK.addAndGet(l);
    }

    public static long getAdena() {
        return awK.get();
    }

    static {
        awI = new AtomicLong(0L);
        awK = new AtomicLong(0L);
        awG.set(ServerVariables.getLong("taxsum", 0L));
        awI.set(ServerVariables.getLong("rouletteSum", 0L));
    }
}

