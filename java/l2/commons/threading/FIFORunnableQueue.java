/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.threading;

import java.util.Queue;

public abstract class FIFORunnableQueue<T extends Runnable>
implements Runnable {
    private static final int NONE = 0;
    private static final int qT = 1;
    private static final int qU = 2;
    private int _state = 0;
    private final Queue<T> qW;

    public FIFORunnableQueue(Queue<T> queue) {
        this.qW = queue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(T t) {
        this.qW.add(t);
        FIFORunnableQueue fIFORunnableQueue = this;
        synchronized (fIFORunnableQueue) {
            if (this._state != 0) {
                return;
            }
            this._state = 1;
        }
        this.execute();
    }

    protected abstract void execute();

    public void clear() {
        this.qW.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Runnable runnable = this;
        synchronized (runnable) {
            if (this._state == 2) {
                return;
            }
            this._state = 2;
        }
        try {
            while ((runnable = (Runnable)this.qW.poll()) != null) {
                runnable.run();
            }
        }
        finally {
            runnable = this;
            synchronized (runnable) {
                this._state = 0;
            }
        }
    }
}

