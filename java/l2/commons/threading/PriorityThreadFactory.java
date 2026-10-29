/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory
implements ThreadFactory {
    private int rY;
    private String _name;
    private AtomicInteger rZ = new AtomicInteger(1);
    private ThreadGroup sa;

    public PriorityThreadFactory(String string, int n) {
        this.rY = n;
        this._name = string;
        this.sa = new ThreadGroup(this._name);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(this.sa, runnable);
        thread.setName(this._name + "-" + this.rZ.getAndIncrement());
        thread.setPriority(this.rY);
        return thread;
    }

    public ThreadGroup getGroup() {
        return this.sa;
    }
}

