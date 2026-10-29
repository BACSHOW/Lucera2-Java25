/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.util.concurrent.locks;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class ReentrantLock {
    private static final AtomicIntegerFieldUpdater<ReentrantLock> tg = AtomicIntegerFieldUpdater.newUpdater(ReentrantLock.class, "state");
    private Thread th;
    private volatile int state;

    private final int getState() {
        return this.state;
    }

    private void setState(int n) {
        this.state = n;
    }

    private boolean compareAndSetState(int n, int n2) {
        return tg.compareAndSet(this, n, n2);
    }

    private Thread getExclusiveOwnerThread() {
        return this.th;
    }

    private void setExclusiveOwnerThread(Thread thread) {
        this.th = thread;
    }

    public void lock() {
        if (this.compareAndSetState(0, 1)) {
            this.setExclusiveOwnerThread(Thread.currentThread());
        } else {
            while (!this.tryLock()) {
            }
        }
    }

    public boolean tryLock() {
        Thread thread = Thread.currentThread();
        int n = this.getState();
        if (n == 0) {
            if (this.compareAndSetState(0, 1)) {
                this.setExclusiveOwnerThread(thread);
                return true;
            }
        } else if (thread == this.getExclusiveOwnerThread()) {
            int n2 = n + 1;
            if (n2 < 0) {
                throw new Error("Maximum lock count exceeded");
            }
            this.setState(n2);
            return true;
        }
        return false;
    }

    public boolean unlock() {
        int n = this.getState() - 1;
        if (Thread.currentThread() != this.getExclusiveOwnerThread()) {
            throw new IllegalMonitorStateException();
        }
        boolean bl = false;
        if (n == 0) {
            bl = true;
            this.setExclusiveOwnerThread(null);
        }
        this.setState(n);
        return bl;
    }
}

