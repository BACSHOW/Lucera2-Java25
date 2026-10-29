package l2.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class ReceivablePacket<T extends MMOClient>
extends l2.commons.net.nio.ReceivablePacket<T> {
    protected T _client;
    protected ByteBuffer _buf;

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this._buf = byteBuffer;
    }

    @Override
    protected ByteBuffer getByteBuffer() {
        return this._buf;
    }

    public void setClient(T t) {
        this._client = t;
    }

    @Override
    public T getClient() {
        return this._client;
    }

    /**
     * Lê e consome os campos do pacote para o cliente e buffer fornecidos (usado pelo Netty).
     */
    public boolean readFrom(ByteBuffer byteBuffer, T client) {
        this._buf = byteBuffer;
        this._client = client;
        return this.read();
    }

    @Override
    protected abstract boolean read();
}
