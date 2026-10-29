package l2.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class SendablePacket<T extends MMOClient> extends l2.commons.net.nio.SendablePacket<T> {
    private static final Object NULL_CLIENT_SENTINEL = new Object();
    private static final ThreadLocal<java.util.ArrayDeque<ByteBuffer>> EXTERNAL_BUFFERS = ThreadLocal.withInitial(java.util.ArrayDeque::new);
    private static final ThreadLocal<java.util.ArrayDeque<Object>> EXTERNAL_CLIENTS = ThreadLocal.withInitial(java.util.ArrayDeque::new);

    @Override
    protected ByteBuffer getByteBuffer() {
        ByteBuffer buf = EXTERNAL_BUFFERS.get().peek();
        if (buf != null) {
            return buf;
        }
        Thread curr = Thread.currentThread();
        if (curr instanceof SelectorThread) {
            return ((SelectorThread) curr).getWriteBuffer();
        }
        throw new IllegalStateException("No write buffer bound to thread " + curr.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getClient() {
        Object client = EXTERNAL_CLIENTS.get().peek();
        if (client != null && client != NULL_CLIENT_SENTINEL) {
            return (T) client;
        }
        Thread curr = Thread.currentThread();
        if (curr instanceof SelectorThread) {
            return (T) ((SelectorThread) curr).getWriteClient();
        }
        return null;
    }

    /**
     * Entrada pública de serialização para transportes fora de SelectorThread (Netty).
     * Vincula o buffer e o cliente destino durante o write(), limpando no finally.
     */
    public final boolean writeTo(ByteBuffer dst, T client) {
        EXTERNAL_BUFFERS.get().push(dst);
        EXTERNAL_CLIENTS.get().push(client != null ? client : NULL_CLIENT_SENTINEL);
        try {
            return this.write();
        } finally {
            EXTERNAL_BUFFERS.get().pop();
            EXTERNAL_CLIENTS.get().pop();
        }
    }

    @Override
    protected abstract boolean write();
}
