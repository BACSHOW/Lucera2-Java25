/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.threading;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import l2.commons.collections.LazyArrayList;
import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SteppingRunnableQueueManager
implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(SteppingRunnableQueueManager.class);
    protected final long tickPerStepInMillis;
    private final List<SteppingScheduledFuture<?>> si = new CopyOnWriteArrayList();
    private final AtomicBoolean sj = new AtomicBoolean();

    public SteppingRunnableQueueManager(long l) {
        this.tickPerStepInMillis = l;
    }

    public SteppingScheduledFuture<?> schedule(Runnable runnable, long l) {
        return this.a(runnable, l, l, false);
    }

    public SteppingScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long l, long l2) {
        return this.a(runnable, l, l2, true);
    }

    private SteppingScheduledFuture<?> a(Runnable runnable, long l, long l2, boolean bl) {
        long l3 = this.e(l);
        long l4 = this.e(l2);
        SteppingScheduledFuture steppingScheduledFuture = new SteppingScheduledFuture(runnable, l3, l4, bl);
        this.si.add(steppingScheduledFuture);
        return steppingScheduledFuture;
    }

    private long e(long l) {
        return (l = Math.max(0L, l)) % this.tickPerStepInMillis > this.tickPerStepInMillis / 2L ? l / this.tickPerStepInMillis + 1L : (l < this.tickPerStepInMillis ? 1L : l / this.tickPerStepInMillis);
    }

    @Override
    public void run() {
        try {
            if (!this.sj.compareAndSet(false, true)) {
                _log.warn("Slow running queue, managed by " + this + ", queue size : " + this.si.size() + "!");
                return;
            }
            try {
                if (this.si.isEmpty()) {
                    return;
                }
                for (SteppingScheduledFuture<?> steppingScheduledFuture : this.si) {
                    if (steppingScheduledFuture.isDone()) continue;
                    steppingScheduledFuture.run();
                }
            }
            finally {
                this.sj.set(false);
            }
        }
        catch (Throwable throwable) {
            _log.error("Exception in stepped queue manager", throwable);
        }
    }

    public void purge() {
        LazyArrayList<SteppingScheduledFuture<?>> lazyArrayList = LazyArrayList.newInstance();
        for (SteppingScheduledFuture<?> steppingScheduledFuture : this.si) {
            if (!steppingScheduledFuture.isDone()) continue;
            lazyArrayList.add(steppingScheduledFuture);
        }
        this.si.removeAll(lazyArrayList);
        LazyArrayList.recycle(lazyArrayList);
    }

    public CharSequence getStats() {
        StringBuilder stringBuilder = new StringBuilder();
        TreeMap<String, MutableLong> treeMap = new TreeMap<String, MutableLong>();
        int n = 0;
        int n2 = 0;
        for (SteppingScheduledFuture<?> steppingScheduledFuture : this.si) {
            if (steppingScheduledFuture.isDone()) {
                ++n2;
                continue;
            }
            ++n;
            MutableLong mutableLong = (MutableLong)treeMap.get(((SteppingScheduledFuture)steppingScheduledFuture).sk.getClass().getName());
            if (mutableLong == null) {
                mutableLong = new MutableLong(1L);
                treeMap.put(((SteppingScheduledFuture)steppingScheduledFuture).sk.getClass().getName(), mutableLong);
                continue;
            }
            mutableLong.increment();
        }
        for (Map.Entry entry : treeMap.entrySet()) {
            stringBuilder.append("\t").append((String)entry.getKey()).append(" : ").append(((MutableLong)entry.getValue()).longValue()).append("\n");
        }
        stringBuilder.append("Scheduled: ....... ").append(n).append("\n");
        stringBuilder.append("Done/Cancelled: .. ").append(n2).append("\n");
        return stringBuilder;
    }

    public class SteppingScheduledFuture<V>
    implements RunnableScheduledFuture<V> {
        private final Runnable sk;
        private final long sl;
        private final boolean sm;
        private long sn;
        private boolean so;

        public SteppingScheduledFuture(Runnable runnable, long l, long l2, boolean bl) {
            this.sk = runnable;
            this.sn = l;
            this.sl = l2;
            this.sm = bl;
        }

        @Override
        public void run() {
            if (--this.sn == 0L) {
                try {
                    this.sk.run();
                }
                catch (Throwable throwable) {
                    _log.error("Exception in a Runnable execution:", throwable);
                }
                finally {
                    if (this.sm) {
                        this.sn = this.sl;
                    }
                }
            }
        }

        @Override
        public boolean isDone() {
            return this.so || !this.sm && this.sn == 0L;
        }

        @Override
        public boolean isCancelled() {
            return this.so;
        }

        @Override
        public boolean cancel(boolean bl) {
            this.so = true;
            return true;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }

        @Override
        public long getDelay(TimeUnit timeUnit) {
            return timeUnit.convert(this.sn * SteppingRunnableQueueManager.this.tickPerStepInMillis, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed delayed) {
            return 0;
        }

        @Override
        public boolean isPeriodic() {
            return this.sm;
        }
    }
}

