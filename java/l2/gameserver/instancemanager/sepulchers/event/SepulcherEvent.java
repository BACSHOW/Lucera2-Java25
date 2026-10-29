/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers.event;

public interface SepulcherEvent {
    default public boolean singleCall() {
        return true;
    }
}

