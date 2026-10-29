/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.idfactory;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.math.PrimeFinder;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.idfactory.IdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitSetIDFactory
extends IdFactory {
    private static final Logger _log = LoggerFactory.getLogger(BitSetIDFactory.class);
    private BitSet zU;
    private AtomicInteger zV;
    private AtomicInteger zW;

    protected BitSetIDFactory() {
        this.initialize();
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new BitSetCapacityCheck(), 30000L, 30000L);
    }

    private void initialize() {
        try {
            this.zU = new BitSet(PrimeFinder.nextPrime(100000));
            this.zU.clear();
            this.zV = new AtomicInteger(0x6FFFFFFF);
            for (int n : this.extractUsedObjectIDTable()) {
                int n2 = n - 0x10000000;
                if (n2 < 0) {
                    _log.warn("Object ID " + n + " in DB is less than minimum ID of " + 0x10000000);
                    continue;
                }
                this.zU.set(n - 0x10000000);
                this.zV.decrementAndGet();
            }
            this.zW = new AtomicInteger(this.zU.nextClearBit(0));
            this.initialized = true;
            _log.info("IdFactory: " + this.zU.size() + " id's available.");
        }
        catch (Exception exception) {
            this.initialized = false;
            _log.error("BitSet ID Factory could not be initialized correctly!", exception);
        }
    }

    @Override
    public synchronized void releaseId(int n) {
        if (n - 0x10000000 > -1) {
            this.zU.clear(n - 0x10000000);
            this.zV.incrementAndGet();
            super.releaseId(n);
        } else {
            _log.warn("BitSet ID Factory: release objectID " + n + " failed (< " + 0x10000000 + ")");
        }
    }

    @Override
    public synchronized int getNextId() {
        int n = this.zW.get();
        this.zU.set(n);
        this.zV.decrementAndGet();
        int n2 = this.zU.nextClearBit(n);
        if (n2 < 0) {
            n2 = this.zU.nextClearBit(0);
        }
        if (n2 < 0) {
            if (this.zU.size() < 0x6FFFFFFF) {
                this.increaseBitSetCapacity();
            } else {
                throw new NullPointerException("Ran out of valid Id's.");
            }
        }
        this.zW.set(n2);
        return n + 0x10000000;
    }

    @Override
    public synchronized int size() {
        return this.zV.get();
    }

    protected synchronized int usedIdCount() {
        return this.size() - 0x10000000;
    }

    protected synchronized boolean reachingBitSetCapacity() {
        return PrimeFinder.nextPrime(this.usedIdCount() * 11 / 10) > this.zU.size();
    }

    protected synchronized void increaseBitSetCapacity() {
        BitSet bitSet = new BitSet(PrimeFinder.nextPrime(this.usedIdCount() * 11 / 10));
        bitSet.or(this.zU);
        this.zU = bitSet;
    }

    public class BitSetCapacityCheck
    extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            if (BitSetIDFactory.this.reachingBitSetCapacity()) {
                BitSetIDFactory.this.increaseBitSetCapacity();
            }
        }
    }
}

