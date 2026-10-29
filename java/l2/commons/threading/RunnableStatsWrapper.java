/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.threading;

import l2.commons.threading.RunnableStatsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableStatsWrapper
implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(RunnableStatsWrapper.class);
    private final Runnable sh;

    RunnableStatsWrapper(Runnable runnable) {
        this.sh = runnable;
    }

    public static Runnable wrap(Runnable runnable) {
        return new RunnableStatsWrapper(runnable);
    }

    @Override
    public void run() {
        RunnableStatsWrapper.execute(this.sh);
    }

    public static void execute(Runnable runnable) {
        long l = System.nanoTime();
        try {
            runnable.run();
            RunnableStatsManager.getInstance().handleStats(runnable.getClass(), System.nanoTime() - l);
        }
        catch (Exception exception) {
            _log.error("Exception in a Runnable execution:", exception);
        }
    }
}

