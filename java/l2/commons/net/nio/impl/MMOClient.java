/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.net.nio.impl;

import java.nio.ByteBuffer;
import l2.commons.net.nio.impl.MMOConnection;

public abstract class MMOClient<T extends MMOConnection> {
    private T qB;
    private boolean qC;

    public MMOClient(T t) {
        this.qB = t;
    }

    protected void setConnection(T t) {
        this.qB = t;
    }

    public T getConnection() {
        return this.qB;
    }

    public boolean isAuthed() {
        return this.qC;
    }

    public void setAuthed(boolean bl) {
        this.qC = bl;
    }

    public void closeNow(boolean bl) {
        if (this.isConnected()) {
            ((MMOConnection)this.qB).closeNow();
        }
    }

    public void closeLater() {
        if (this.isConnected()) {
            ((MMOConnection)this.qB).closeLater();
        }
    }

    public boolean isConnected() {
        return this.qB != null && !((MMOConnection)this.qB).isClosed();
    }

    public abstract boolean decrypt(ByteBuffer var1, int var2);

    public abstract boolean encrypt(ByteBuffer var1, int var2);

    protected void onDisconnection() {
    }

    protected void onForcedDisconnection() {
    }

    public String getIpAddr() {
        return ((MMOConnection)this.getConnection()).getIpAddr();
    }
}

