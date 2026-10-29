/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.net.nio.impl;

import java.util.concurrent.atomic.AtomicLong;

public class SelectorStats {
    private AtomicLong qY = new AtomicLong();
    private AtomicLong qZ = new AtomicLong();
    private AtomicLong ra = new AtomicLong();
    private AtomicLong rb = new AtomicLong();
    private AtomicLong rc = new AtomicLong();
    private AtomicLong rd = new AtomicLong();
    private AtomicLong re = new AtomicLong();
    private AtomicLong rf = new AtomicLong();
    private AtomicLong rg = new AtomicLong();

    public void increaseOpenedConnections() {
        if (this.qZ.incrementAndGet() > this.ra.get()) {
            this.ra.incrementAndGet();
        }
        this.qY.incrementAndGet();
    }

    public void decreseOpenedConnections() {
        this.qZ.decrementAndGet();
    }

    public void increaseIncomingBytes(int n) {
        if ((long)n > this.rf.get()) {
            this.rf.set(n);
        }
        this.rb.addAndGet(n);
    }

    public void increaseOutgoingBytes(int n) {
        if ((long)n > this.rg.get()) {
            this.rg.set(n);
        }
        this.rc.addAndGet(n);
    }

    public void increaseIncomingPacketsCount() {
        this.rd.incrementAndGet();
    }

    public void increaseOutgoingPacketsCount() {
        this.re.incrementAndGet();
    }

    public long getTotalConnections() {
        return this.qY.get();
    }

    public long getCurrentConnections() {
        return this.qZ.get();
    }

    public long getMaximumConnections() {
        return this.ra.get();
    }

    public long getIncomingBytesTotal() {
        return this.rb.get();
    }

    public long getOutgoingBytesTotal() {
        return this.rc.get();
    }

    public long getIncomingPacketsTotal() {
        return this.rd.get();
    }

    public long getOutgoingPacketsTotal() {
        return this.re.get();
    }

    public long getMaxBytesPerRead() {
        return this.rf.get();
    }

    public long getMaxBytesPerWrite() {
        return this.rg.get();
    }
}

