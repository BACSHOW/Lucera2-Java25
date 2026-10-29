/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

public enum AcquireType {
    NORMAL,
    FISHING,
    CLAN,
    NOBLES,
    HERO,
    CLAN_LEADER;

    public static final AcquireType[] VALUES;

    static {
        VALUES = AcquireType.values();
    }
}

