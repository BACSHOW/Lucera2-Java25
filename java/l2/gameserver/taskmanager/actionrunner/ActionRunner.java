/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager.actionrunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.logging.LoggerObject;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.taskmanager.actionrunner.ActionWrapper;
import l2.gameserver.taskmanager.actionrunner.tasks.AutomaticTask;
import l2.gameserver.taskmanager.actionrunner.tasks.DeleteExpiredMailTask;
import l2.gameserver.taskmanager.actionrunner.tasks.DeleteExpiredVarsTask;

public class ActionRunner
extends LoggerObject {
    private static ActionRunner arz = new ActionRunner();
    private Map<String, List<ActionWrapper>> arA = new HashMap<String, List<ActionWrapper>>();
    private final Lock arB = new ReentrantLock();

    public static ActionRunner getInstance() {
        return arz;
    }

    private ActionRunner() {
        this.register(new DeleteExpiredVarsTask());
        this.register(new DeleteExpiredMailTask());
    }

    public void register(AutomaticTask automaticTask) {
        this.register(automaticTask.reCalcTime(true), automaticTask);
    }

    public void register(long l, ActionWrapper actionWrapper) {
        if (l == 0L) {
            this.info("Try register " + actionWrapper.getName() + " not defined time.");
            return;
        }
        if (l <= System.currentTimeMillis()) {
            ThreadPoolManager.getInstance().execute(actionWrapper);
            return;
        }
        this.addScheduled(actionWrapper.getName(), actionWrapper, l - System.currentTimeMillis());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addScheduled(String string, ActionWrapper actionWrapper, long l) {
        this.arB.lock();
        try {
            String string2 = string.toLowerCase();
            List<ActionWrapper> list = this.arA.get(string2);
            if (list == null) {
                list = new ArrayList<ActionWrapper>();
                this.arA.put(string2, list);
            }
            actionWrapper.schedule(l);
            list.add(actionWrapper);
        }
        finally {
            this.arB.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void remove(String string, ActionWrapper actionWrapper) {
        this.arB.lock();
        try {
            String string2 = string.toLowerCase();
            List<ActionWrapper> list = this.arA.get(string2);
            if (list == null) {
                return;
            }
            list.remove(actionWrapper);
            if (list.isEmpty()) {
                this.arA.remove(string2);
            }
        }
        finally {
            this.arB.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear(String string) {
        this.arB.lock();
        try {
            String string2 = string.toLowerCase();
            List<ActionWrapper> list = this.arA.remove(string2);
            if (list == null) {
                return;
            }
            for (ActionWrapper actionWrapper : list) {
                actionWrapper.cancel();
            }
            list.clear();
        }
        finally {
            this.arB.unlock();
        }
    }

    public void info() {
        this.arB.lock();
        try {
            for (Map.Entry<String, List<ActionWrapper>> entry : this.arA.entrySet()) {
                this.info("Name: " + entry.getKey() + "; size: " + entry.getValue().size());
            }
        }
        finally {
            this.arB.unlock();
        }
    }
}

