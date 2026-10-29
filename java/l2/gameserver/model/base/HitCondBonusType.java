/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

public enum HitCondBonusType {
    ahead,
    side,
    back,
    high,
    low,
    dark,
    rain;

    public static final HitCondBonusType[] VALUES;

    static {
        VALUES = HitCondBonusType.values();
    }
}

