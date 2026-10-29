/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.dao;

public interface JdbcEntityStats {
    public long getLoadCount();

    public long getInsertCount();

    public long getUpdateCount();

    public long getDeleteCount();
}

