/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

public enum Language {
    ENGLISH("en"),
    RUSSIAN("ru");

    public static final Language[] VALUES;
    private String awL;

    private Language(String string2) {
        this.awL = string2;
    }

    public String getShortName() {
        return this.awL;
    }

    static {
        VALUES = Language.values();
    }
}

