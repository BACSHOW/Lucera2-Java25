/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.net.nio.impl;

import java.net.Socket;
import java.nio.channels.SelectionKey;
import l2.commons.net.nio.impl.MMOClient;
import l2.commons.net.nio.impl.MMOConnection;
import l2.commons.net.nio.impl.SelectorThread;

public class HaProxyMMOConnection<T extends MMOClient>
extends MMOConnection<T> {
    private boolean qv = false;

    public HaProxyMMOConnection(SelectorThread<T> selectorThread, Socket socket, SelectionKey selectionKey) {
        super(selectorThread, socket, selectionKey);
    }

    @Override
    protected void onDisconnection() {
        Object t = this.getClient();
        if (t != null) {
            ((MMOClient)t).onDisconnection();
        }
    }

    @Override
    protected void onForcedDisconnection() {
        Object t = this.getClient();
        if (t != null) {
            ((MMOClient)t).onForcedDisconnection();
        }
    }

    protected boolean isHaProxyInitiated() {
        return this.qv;
    }

    protected HaProxyMMOConnection<T> setHaProxyInitiated(boolean bl) {
        this.qv = bl;
        return this;
    }
}

