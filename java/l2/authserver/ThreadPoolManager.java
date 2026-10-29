/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import l2.commons.threading.RunnableImpl;

public class ThreadPoolManager {
    private static final long nl = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2L;
    private static final ThreadPoolManager nm = new ThreadPoolManager();
    private final ScheduledThreadPoolExecutor nn = new ScheduledThreadPoolExecutor(1);
    private final ThreadPoolExecutor no = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static final ThreadPoolManager getInstance() {
        return nm;
    }

    private ThreadPoolManager() {
        this.scheduleAtFixedRate(new RunnableImpl(){

            @Override
            public void runImpl() {
                ThreadPoolManager.this.no.purge();
                ThreadPoolManager.this.nn.purge();
            }
        }, 600000L, 600000L);
    }

    private final long a(long l) {
        return Math.max(0L, Math.min(nl, l));
    }

    public void execute(Runnable runnable) {
        this.no.execute(runnable);
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long l) {
        return this.nn.schedule(runnable, this.a(l), TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long l, long l2) {
        return this.nn.scheduleAtFixedRate(runnable, this.a(l), this.a(l2), TimeUnit.MILLISECONDS);
    }

    public void shutdown() throws InterruptedException {
        try {
            this.nn.shutdown();
            this.nn.awaitTermination(1L, TimeUnit.SECONDS);
        }
        finally {
            this.no.shutdown();
            this.no.awaitTermination(1L, TimeUnit.SECONDS);
        }
    }

    public List<Runnable> shutdownNow() throws InterruptedException {
        LinkedList<Runnable> linkedList = new LinkedList<Runnable>();
        try {
            linkedList.addAll(this.nn.shutdownNow());
        }
        finally {
            linkedList.addAll(this.no.shutdownNow());
        }
        return linkedList;
    }
}

