/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.net.nio;

import l2.commons.net.nio.AbstractPacket;

public abstract class ReceivablePacket<T>
extends AbstractPacket<T>
implements Runnable {
    protected int getAvaliableBytes() {
        return this.getByteBuffer().remaining();
    }

    protected void readB(byte[] byArray) {
        this.getByteBuffer().get(byArray);
    }

    protected void readB(byte[] byArray, int n, int n2) {
        this.getByteBuffer().get(byArray, n, n2);
    }

    protected int readC() {
        return this.getByteBuffer().get() & 0xFF;
    }

    protected int readH() {
        return this.getByteBuffer().getShort() & 0xFFFF;
    }

    protected int readD() {
        return this.getByteBuffer().getInt();
    }

    protected long readUD() {
        return (long)this.getByteBuffer().getInt() & 0xFFFFFFFFL;
    }

    protected long readQ() {
        return this.getByteBuffer().getLong();
    }

    protected double readF() {
        return this.getByteBuffer().getDouble();
    }

    protected String readS() {
        char c;
        StringBuilder stringBuilder = new StringBuilder();
        while ((c = this.getByteBuffer().getChar()) != '\u0000') {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    protected abstract boolean read();
}

