/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import java.util.concurrent.ScheduledFuture;
import l2.gameserver.taskmanager.TaskManager;

public abstract class Task {
    public abstract void init();

    public ScheduledFuture<?> launchSpecial(TaskManager.ExecutedTask executedTask) {
        return null;
    }

    public abstract String getName();

    public abstract void onTimeElapsed(TaskManager.ExecutedTask var1);

    public void onDestroy() {
    }
}

