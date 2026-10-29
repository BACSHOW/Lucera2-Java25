/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.dao;

public enum JdbcEntityState {
    CREATED(true, false, false, false),
    STORED(false, true, false, true),
    UPDATED(false, true, true, true),
    DELETED(false, false, false, false);

    private final boolean pY;
    private final boolean pZ;
    private final boolean qa;
    private final boolean qb;

    private JdbcEntityState(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        this.pY = bl;
        this.pZ = bl2;
        this.qa = bl3;
        this.qb = bl4;
    }

    public boolean isSavable() {
        return this.pY;
    }

    public boolean isDeletable() {
        return this.pZ;
    }

    public boolean isUpdatable() {
        return this.qa;
    }

    public boolean isPersisted() {
        return this.qb;
    }
}

