package l2.gameserver.network.l2.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import l2.commons.net.nio.impl.IMMOExecutor;
import l2.commons.net.nio.impl.MMOExecutableQueue;
import l2.commons.net.nio.impl.ReceivablePacket;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.network.l2.GameClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import l2.gameserver.network.l2.netty.disruptor.OutboundFloodQueue;

/**
 * Wrapper de conexão por canal do Netty.
 * Mantém fila de execução ordenada (MMOExecutableQueue) para garantir processamento sequencial estrito por jogador.
 */
public final class NettyGameConnection {
    private static final AttributeKey<NettyGameConnection> CONNECTION_KEY = AttributeKey.valueOf("l2.connection");
    private static final int MAX_BATCH_PACKETS_BEFORE_FLUSH = 64;

    private final Channel channel;
    private final MMOExecutableQueue<GameClient> packetQueue;
    private GameClient client;
    private String overrideIp = null;
    private final OutboundFloodQueue outboundFloodQueue;
    private final OutboundFloodQueue.State outboundFloodState = new OutboundFloodQueue.State();
    private final AtomicInteger batchDepth = new AtomicInteger();
    private final AtomicInteger batchPacketCount = new AtomicInteger();

    public NettyGameConnection(Channel channel) {
        this(channel, runnable -> ThreadPoolManager.getInstance().execute(runnable));
    }

    public NettyGameConnection(Channel channel, IMMOExecutor<GameClient> executor) {
        this.channel = channel;
        this.channel.attr(CONNECTION_KEY).set(this);
        this.packetQueue = new MMOExecutableQueue<>(executor != null ? executor : runnable -> ThreadPoolManager.getInstance().execute(runnable));
        this.outboundFloodQueue = new OutboundFloodQueue(this, 1024);
    }

    public static NettyGameConnection of(Channel channel) {
        return channel.attr(CONNECTION_KEY).get();
    }

    public Channel getChannel() {
        return this.channel;
    }

    public boolean isOpen() {
        return this.channel.isOpen();
    }

    public boolean isConnected() {
        return this.channel.isActive();
    }

    public boolean isWritable() {
        return this.channel.isWritable();
    }

    public SocketAddress getRemoteAddress() {
        return this.channel.remoteAddress();
    }

    public InetAddress getAddress() {
        SocketAddress sa = this.channel.remoteAddress();
        if (sa instanceof InetSocketAddress) {
            return ((InetSocketAddress) sa).getAddress();
        }
        return null;
    }

    public String getIpAddr() {
        if (this.overrideIp != null) {
            return this.overrideIp;
        }
        InetAddress addr = this.getAddress();
        return addr != null ? addr.getHostAddress() : "?.?.?.?";
    }

    public void setOverrideIp(String ip) {
        this.overrideIp = ip;
    }

    public void attachClient(GameClient client) {
        this.client = client;
    }

    public GameClient getClient() {
        return this.client;
    }

    /**
     * Enfileira o pacote de entrada na fila sequencial do jogador (executa em ordem FIFO garantida).
     */
    public void recvPacket(ReceivablePacket<GameClient> packet) {
        this.packetQueue.add(packet);
    }

    /**
     * Envia o payload pré-serializado. O L2PacketEncoder cuidará da encriptação e do header de 2 bytes LE.
     */
    public void send(byte[] payload) {
        if (!this.channel.isActive() || payload == null || payload.length == 0) {
            return;
        }
        this.channel.write(Unpooled.wrappedBuffer(payload));
        if (this.batchDepth.get() == 0) {
            this.channel.flush();
        } else if (this.batchPacketCount.incrementAndGet() >= MAX_BATCH_PACKETS_BEFORE_FLUSH) {
            this.batchPacketCount.set(0);
            this.channel.flush();
        }
    }

    public void startBatch() {
        this.batchDepth.incrementAndGet();
    }

    public void endBatch() {
        if (this.batchDepth.decrementAndGet() <= 0) {
            this.batchDepth.set(0);
            this.batchPacketCount.set(0);
            if (this.channel.isActive()) this.channel.flush();
        }
    }

    public void sendFlood(byte[] payload, OutboundFloodQueue.Kind kind, int objectId) {
        this.outboundFloodQueue.publish(payload, kind, objectId);
    }

    public void acceptFloodEvent(byte[] payload, OutboundFloodQueue.Kind kind, int objectId) {
        this.outboundFloodState.accept(this.channel, payload, kind, objectId);
    }

    public void sendBatch(List<byte[]> payloads) {
        this.writeBatch(payloads, false);
    }

    public void sendAndClose(List<byte[]> payloads) {
        this.writeBatch(payloads, true);
    }

    private void writeBatch(List<byte[]> payloads, boolean closeAfterFlush) {
        if (payloads == null || payloads.isEmpty() || !this.channel.isActive()) {
            if (closeAfterFlush) {
                this.close();
            }
            return;
        }

        this.channel.eventLoop().execute(() -> {
            if (!this.channel.isActive()) {
                return;
            }

            if (closeAfterFlush) {
                byte[] lastPayload = null;
                for (byte[] payload : payloads) {
                    if (payload != null && payload.length > 0) {
                        if (lastPayload != null) {
                            this.channel.write(Unpooled.wrappedBuffer(lastPayload));
                        }
                        lastPayload = payload;
                    }
                }
                if (lastPayload == null) {
                    this.channel.close();
                } else {
                    this.channel.writeAndFlush(Unpooled.wrappedBuffer(lastPayload))
                            .addListener(future -> this.channel.close());
                }
            } else {
                for (byte[] payload : payloads) {
                    if (payload != null && payload.length > 0) {
                        this.channel.write(Unpooled.wrappedBuffer(payload));
                    }
                }
                this.channel.flush();
            }
        });
    }

    public void close() {
        this.outboundFloodQueue.shutdown();
        if (this.channel.isActive()) {
            this.channel.close();
        }
    }
}
