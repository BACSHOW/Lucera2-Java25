/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm;

public enum ServerType {
    NORMAL,
    RELAX,
    TEST,
    NO_LABEL,
    RESTRICTED,
    EVENT,
    FREE;

    private int YK = 1 << this.ordinal();

    public int getMask() {
        return this.YK;
    }
}

