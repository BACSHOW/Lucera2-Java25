/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager.actionrunner.tasks;

import l2.gameserver.taskmanager.actionrunner.ActionRunner;
import l2.gameserver.taskmanager.actionrunner.ActionWrapper;

public abstract class AutomaticTask
extends ActionWrapper {
    public static final String TASKS = "automatic_tasks";

    public AutomaticTask() {
        super(TASKS);
    }

    public abstract void doTask() throws Exception;

    public abstract long reCalcTime(boolean var1);

    @Override
    public void runImpl0() throws Exception {
        try {
            this.doTask();
        }
        finally {
            ActionRunner.getInstance().register(this.reCalcTime(false), this);
        }
    }
}

