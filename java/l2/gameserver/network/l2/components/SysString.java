/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.components;

public enum SysString {
    PASSENGER_BOAT_INFO(801),
    PREVIOUS(1037),
    NEXT(1038);

    private static final SysString[] ads;
    private final int _id;

    private SysString(int n2) {
        this._id = n2;
    }

    public int getId() {
        return this._id;
    }

    public static SysString valueOf2(String string) {
        for (SysString sysString : ads) {
            if (!sysString.name().equals(string)) continue;
            return sysString;
        }
        return null;
    }

    public static SysString valueOf(int n) {
        for (SysString sysString : ads) {
            if (sysString.getId() != n) continue;
            return sysString;
        }
        return null;
    }

    static {
        ads = SysString.values();
    }
}

