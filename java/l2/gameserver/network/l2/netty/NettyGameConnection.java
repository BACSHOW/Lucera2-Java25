package l2.gameserver.network.l2.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.AttributeKey;
import l2.commons.net.nio.impl.IMMOExecutor;
import l2.commons.net.nio.impl.MMOExecutableQueue;
import l2.commons.net.nio.impl.ReceivablePacket;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.network.l2.GameClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wrapper de conexão por canal do Netty.
 * Mantém fila de execução ordenada (MMOExecutableQueue) para garantir processamento sequencial estrito por jogador.
 */
public final class NettyGameConnection {
    private static final AttributeKey<NettyGameConnection> CONNECTION_KEY = AttributeKey.valueOf("l2.connection");
    private static final int MAX_BATCH_PACKETS_BEFORE_FLUSH = 64;
    /** Nome do limitador de velocidade de teste (//netslow) no pipeline do canal. */
    private static final String NETSLOW = "netslow";
    /** Conexoes abertas, para as metricas da fila de envio. */
    private static final Set<NettyGameConnection> OPEN = ConcurrentHashMap.newKeySet();
    /** Ressincronizacao no maximo a cada 2 s por cliente (ela mesma pode encher uma conexao lenta de novo). */
    private static final long RESYNC_MIN_INTERVAL_MS = 2000L;

    private final Channel channel;
    private volatile boolean shedPending;
    private final AtomicBoolean resyncScheduled = new AtomicBoolean();
    private volatile long lastResyncAt;
    private final MMOExecutableQueue<GameClient> packetQueue;
    private GameClient client;
    private String overrideIp = null;
    private final AtomicInteger batchDepth = new AtomicInteger();
    private final AtomicInteger batchPacketCount = new AtomicInteger();

    public NettyGameConnection(Channel channel) {
        this(channel, runnable -> ThreadPoolManager.getInstance().execute(runnable));
    }

    public NettyGameConnection(Channel channel, IMMOExecutor<GameClient> executor) {
        this.channel = channel;
        this.channel.attr(CONNECTION_KEY).set(this);
        this.packetQueue = new MMOExecutableQueue<>(executor != null ? executor : runnable -> ThreadPoolManager.getInstance().execute(runnable));
        OPEN.add(this);
        this.channel.closeFuture().addListener(f -> OPEN.remove(this));
    }

    // ------------------------------------------------------------------ fila de envio (metricas e //netslow)

    /**
     * Bytes esperando para sair para este cliente: a fila do Netty e, com //netslow ligado, a fila do limitador.
     * Cresce quando o cliente recebe mais devagar do que o servidor envia.
     */
    public long pendingWriteBytes() {
        long n = 0L;
        ChannelOutboundBuffer out = this.channel.unsafe().outboundBuffer();
        if (out != null) {
            n += out.totalPendingWriteBytes();
        }
        ChannelHandler h = this.channel.pipeline().get(NETSLOW);
        if (h instanceof ChannelTrafficShapingHandler shaping) {
            n += shaping.queueSize();
        }
        return n;
    }

    /** Limite de teste em KB/s (0 = sem limite). */
    public int getThrottleKbps() {
        ChannelHandler h = this.channel.pipeline().get(NETSLOW);
        return h instanceof ChannelTrafficShapingHandler shaping ? (int) (shaping.getWriteLimit() / 1024L) : 0;
    }

    /**
     * Teste de conexao lenta (//netslow): limita o envio para este cliente a kbps KB/s; 0 tira o limite.
     * O limitador marca a conexao como cheia no mesmo limite real (NettyWriteHighWaterMark), entao as metricas
     * se comportam como numa conexao lenta de verdade.
     */
    public void setThrottleKbps(int kbps) {
        this.channel.eventLoop().execute(() -> {
            if (!this.channel.isActive()) {
                return;
            }
            if (this.channel.pipeline().get(NETSLOW) != null) {
                this.channel.pipeline().remove(NETSLOW);
            }
            if (kbps > 0) {
                ChannelTrafficShapingHandler shaping = new ChannelTrafficShapingHandler(kbps * 1024L, 0L, 100L, 60_000L);
                shaping.setMaxWriteSize(Config.NETTY_WRITE_HIGH_WATER_MARK);
                // ultimo a tratar a saida: limita os bytes ja criptografados que vao para o socket
                this.channel.pipeline().addFirst(NETSLOW, shaping);
            }
        });
    }

    // ------------------------------------------------------------------ cliente lento (NettySlowClientShedding)

    /** Algum pacote visual foi pulado enquanto a conexao estava cheia: falta ressincronizar. */
    public void markShed() {
        this.shedPending = true;
    }

    /**
     * A conexao voltou a aceitar dados (chamado pelo NettyGameDispatcher). Se pacotes foram pulados, agenda a
     * ressincronizacao fora da thread de rede, respeitando o intervalo minimo.
     */
    public void onWritableAgain() {
        if (!this.shedPending || !this.resyncScheduled.compareAndSet(false, true)) {
            return;
        }
        long wait = Math.max(0L, RESYNC_MIN_INTERVAL_MS - (System.currentTimeMillis() - this.lastResyncAt));
        ThreadPoolManager.getInstance().schedule(() -> {
            this.resyncScheduled.set(false);
            if (!this.channel.isActive() || !this.channel.isWritable()) {
                return; // encheu de novo: a proxima vez que esvaziar agenda outra
            }
            this.shedPending = false;
            this.lastResyncAt = System.currentTimeMillis();
            GameClient c = this.client;
            if (c != null) {
                c.resyncAfterSlowLink();
            }
        }, wait);
    }

    /** {conexoes cheias agora, maior fila em bytes, conexoes com //netslow} para o ServerMetrics. */
    public static long[] queueStats() {
        long full = 0L, max = 0L, slowed = 0L;
        for (NettyGameConnection c : OPEN) {
            try {
                if (!c.channel.isWritable()) {
                    full++;
                }
                if (c.channel.pipeline().get(NETSLOW) != null) {
                    slowed++;
                }
                max = Math.max(max, c.pendingWriteBytes());
            } catch (Throwable ignored) {
                // canal fechando
            }
        }
        return new long[]{full, max, slowed};
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
        if (this.channel.isActive()) {
            this.channel.close();
        }
    }
}
