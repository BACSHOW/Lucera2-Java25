/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.net.nio.impl;

import java.io.IOException;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import l2.commons.net.nio.impl.MMOClient;
import l2.commons.net.nio.impl.MMOExecutableQueue;
import l2.commons.net.nio.impl.ReceivablePacket;
import l2.commons.net.nio.impl.SelectorThread;
import l2.commons.net.nio.impl.SendablePacket;

public class MMOConnection<T extends MMOClient> {
    public static final String NO_IP = "?.?.?.?";
    private final SelectorThread<T> qD;
    private final SelectionKey qE;
    private final Socket qF;
    private final WritableByteChannel qG;
    private final ReadableByteChannel qH;
    private final Queue<SendablePacket<T>> qI;
    private final Queue<ReceivablePacket<T>> qJ;
    private T _client;
    private ByteBuffer qK;
    private ByteBuffer qL;
    private ByteBuffer qM;
    private boolean qN;
    private long qO;
    private boolean qP;
    private long qQ;
    private AtomicBoolean qR = new AtomicBoolean();
    private String qS;

    public MMOConnection(SelectorThread<T> selectorThread, Socket socket, SelectionKey selectionKey) {
        this.qD = selectorThread;
        this.qE = selectionKey;
        this.qF = socket;
        this.qG = socket.getChannel();
        this.qH = socket.getChannel();
        this.qI = new ArrayDeque<SendablePacket<T>>();
        this.qJ = new MMOExecutableQueue<T>(selectorThread.getExecutor());
    }

    protected void setClient(T t) {
        this._client = t;
    }

    public T getClient() {
        return this._client;
    }

    public void recvPacket(ReceivablePacket<T> receivablePacket) {
        if (receivablePacket == null) {
            return;
        }
        if (this.isClosed()) {
            return;
        }
        this.qJ.add(receivablePacket);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendPacket(SendablePacket<T> sendablePacket) {
        if (sendablePacket == null) {
            return;
        }
        MMOConnection mMOConnection = this;
        synchronized (mMOConnection) {
            if (this.isClosed()) {
                return;
            }
            this.qI.add(sendablePacket);
        }
        this.scheduleWriteInterest();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendPacket(SendablePacket<T> ... sendablePacketArray) {
        if (sendablePacketArray == null || sendablePacketArray.length == 0) {
            return;
        }
        MMOConnection mMOConnection = this;
        synchronized (mMOConnection) {
            if (this.isClosed()) {
                return;
            }
            for (SendablePacket<T> sendablePacket : sendablePacketArray) {
                if (sendablePacket == null) continue;
                this.qI.add(sendablePacket);
            }
        }
        this.scheduleWriteInterest();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendPackets(List<? extends SendablePacket<T>> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        MMOConnection mMOConnection = this;
        synchronized (mMOConnection) {
            if (this.isClosed()) {
                return;
            }
            for (int i = 0; i < list.size(); ++i) {
                SendablePacket<T> sendablePacket = list.get(i);
                if (sendablePacket == null) continue;
                this.qI.add(sendablePacket);
            }
        }
        this.scheduleWriteInterest();
    }

    protected SelectionKey getSelectionKey() {
        return this.qE;
    }

    protected void disableReadInterest() {
        try {
            this.qE.interestOps(this.qE.interestOps() & 0xFFFFFFFE);
        }
        catch (CancelledKeyException cancelledKeyException) {
            // empty catch block
        }
    }

    protected void scheduleWriteInterest() {
        try {
            if (this.qR.compareAndSet(false, true)) {
                this.qQ = System.currentTimeMillis();
            }
        }
        catch (CancelledKeyException cancelledKeyException) {
            // empty catch block
        }
    }

    protected void disableWriteInterest() {
        try {
            if (this.qR.compareAndSet(true, false)) {
                this.qE.interestOps(this.qE.interestOps() & 0xFFFFFFFB);
            }
        }
        catch (CancelledKeyException cancelledKeyException) {
            // empty catch block
        }
    }

    protected void enableWriteInterest() {
        if (this.qR.compareAndSet(true, false)) {
            this.qE.interestOps(this.qE.interestOps() | 4);
        }
    }

    protected boolean isPendingWrite() {
        return this.qR.get();
    }

    public long getPendingWriteTime() {
        return this.qQ;
    }

    public Socket getSocket() {
        return this.qF;
    }

    public WritableByteChannel getWritableChannel() {
        return this.qG;
    }

    public ReadableByteChannel getReadableByteChannel() {
        return this.qH;
    }

    protected Queue<SendablePacket<T>> getSendQueue() {
        return this.qI;
    }

    protected Queue<ReceivablePacket<T>> getRecvQueue() {
        return this.qJ;
    }

    protected void createWriteBuffer(ByteBuffer byteBuffer) {
        if (this.qL == null) {
            this.qL = this.qD.getPooledBuffer();
            this.qL.put(byteBuffer);
        } else {
            ByteBuffer byteBuffer2 = this.qD.getPooledBuffer();
            byteBuffer2.put(byteBuffer);
            int n = byteBuffer2.remaining();
            ((Buffer)this.qL).flip();
            int n2 = this.qL.limit();
            if (n >= this.qL.remaining()) {
                byteBuffer2.put(this.qL);
                this.qD.recycleBuffer(this.qL);
                this.qL = byteBuffer2;
            } else {
                ((Buffer)this.qL).limit(n);
                byteBuffer2.put(this.qL);
                ((Buffer)this.qL).limit(n2);
                this.qL.compact();
                this.qM = this.qL;
                this.qL = byteBuffer2;
            }
        }
    }

    protected boolean hasPendingWriteBuffer() {
        return this.qL != null;
    }

    protected void movePendingWriteBufferTo(ByteBuffer byteBuffer) {
        ((Buffer)this.qL).flip();
        byteBuffer.put(this.qL);
        this.qD.recycleBuffer(this.qL);
        this.qL = this.qM;
        this.qM = null;
    }

    protected void setReadBuffer(ByteBuffer byteBuffer) {
        this.qK = byteBuffer;
    }

    public ByteBuffer getReadBuffer() {
        return this.qK;
    }

    public boolean isClosed() {
        return this.qN || this.qP;
    }

    public boolean isPengingClose() {
        return this.qN;
    }

    public long getPendingCloseTime() {
        return this.qO;
    }

    protected void close() throws IOException {
        this.qP = true;
        this.qF.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeNow() {
        MMOConnection mMOConnection = this;
        synchronized (mMOConnection) {
            if (this.isClosed()) {
                return;
            }
            this.qI.clear();
            this.qN = true;
            this.qO = System.currentTimeMillis();
        }
        this.disableReadInterest();
        this.disableWriteInterest();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close(SendablePacket<T> sendablePacket) {
        MMOConnection mMOConnection = this;
        synchronized (mMOConnection) {
            if (this.isClosed()) {
                return;
            }
            this.qI.clear();
            this.sendPacket(sendablePacket);
            this.qN = true;
            this.qO = System.currentTimeMillis();
        }
        this.disableReadInterest();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeLater() {
        MMOConnection mMOConnection = this;
        synchronized (mMOConnection) {
            if (this.isClosed()) {
                return;
            }
            this.qN = true;
            this.qO = System.currentTimeMillis();
        }
    }

    protected void releaseBuffers() {
        if (this.qL != null) {
            this.qD.recycleBuffer(this.qL);
            this.qL = null;
            if (this.qM != null) {
                this.qD.recycleBuffer(this.qM);
                this.qM = null;
            }
        }
        if (this.qK != null) {
            this.qD.recycleBuffer(this.qK);
            this.qK = null;
        }
    }

    protected void clearQueues() {
        this.qI.clear();
        this.qJ.clear();
    }

    protected void onDisconnection() {
        ((MMOClient)this.getClient()).onDisconnection();
    }

    protected void onForcedDisconnection() {
        ((MMOClient)this.getClient()).onForcedDisconnection();
    }

    public String getIpAddr() {
        return this.qS;
    }

    protected void setIpAddr(String string) {
        this.qS = string;
    }

    public String toString() {
        return "MMOConnection: selector=" + this.qD + "; client=" + this.getClient();
    }
}
