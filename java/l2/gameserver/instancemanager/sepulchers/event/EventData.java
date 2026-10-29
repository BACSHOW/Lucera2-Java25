/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers.event;

import java.util.concurrent.atomic.AtomicBoolean;
import l2.gameserver.instancemanager.sepulchers.event.SepulcherEvent;

public class EventData {
    private final SepulcherEvent BM;
    private final boolean BN;
    private final AtomicBoolean BO = new AtomicBoolean();

    public EventData(SepulcherEvent sepulcherEvent, boolean bl) {
        this.BM = sepulcherEvent;
        this.BN = bl;
    }

    public SepulcherEvent getEvent() {
        return this.BM;
    }

    public AtomicBoolean getCallFlag() {
        return this.BO;
    }

    public boolean isSingleCall() {
        return this.BN;
    }
}

