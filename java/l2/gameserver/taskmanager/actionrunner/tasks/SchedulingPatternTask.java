/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager.actionrunner.tasks;

import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SchedulingPatternTask
extends RunnableImpl {
    protected static final Logger _log = LoggerFactory.getLogger(SchedulingPatternTask.class);
    private final SchedulingPattern arD;

    public SchedulingPatternTask(String string) {
        this.arD = new SchedulingPattern(string);
        this.init();
    }

    public void doTask() {
        long l = System.currentTimeMillis();
        _log.info(this.getClass().getSimpleName() + ": start.");
        this.runTask();
        _log.info(this.getClass().getSimpleName() + ": done in " + (System.currentTimeMillis() - l) + " ms.");
    }

    public long getLeftTime() {
        return this.reCalcTime() - System.currentTimeMillis();
    }

    public long reCalcTime() {
        return this.arD.next(System.currentTimeMillis());
    }

    protected abstract void runTask();

    public void init() {
        ThreadPoolManager.getInstance().schedule(this, this.reCalcTime() - System.currentTimeMillis());
    }

    @Override
    public void runImpl() throws Exception {
        try {
            this.doTask();
        }
        finally {
            this.init();
        }
    }
}

