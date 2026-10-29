/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events;

public enum EventType {
    MAIN_EVENT,
    SIEGE_EVENT,
    PVP_EVENT,
    BOAT_EVENT,
    FUN_EVENT;

    private int Qn = this.ordinal() * 1000;

    public int step() {
        return this.Qn;
    }
}

