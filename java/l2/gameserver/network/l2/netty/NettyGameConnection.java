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
import java.util.concurrent.atomic.AtomicLong;

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
    /** Intervalo para conferir se a fila esvaziou depois de pacotes pulados. */
    private static final long DRAIN_CHECK_MS = 500L;

    private final Channel channel;
    private volatile boolean shedPending;
    /** Correcoes de posicao enviadas / ja entregues ao socket (ValidatePosition). */
    private final AtomicLong correctionsSent = new AtomicLong();
    private volatile long correctionsDelivered;
    private volatile long lastCorrectionDeliveredAt;
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

    // ------------------------------------------------------------------ correcao de posicao em transito

    /**
     * Chamar logo depois de enviar uma correcao de posicao (ValidateLocation). Um buffer vazio entra na fila
     * atras dela: quando ele sai para o socket, a correcao tambem saiu (a fila e em ordem). O buffer vazio nao
     * gera pacote nem mexe na criptografia (o L2PacketEncoder ignora tamanho 0).
     */
    public void markPositionCorrectionSent() {
        long seq = this.correctionsSent.incrementAndGet();
        if (!this.channel.isActive()) {
            return;
        }
        this.channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(f -> {
            if (seq > this.correctionsDelivered) {
                this.correctionsDelivered = seq;
                this.lastCorrectionDeliveredAt = System.currentTimeMillis();
            }
        });
    }

    /**
     * true enquanto a ultima correcao ainda nao saiu para o cliente, ou saiu ha menos de graceMs (o cliente
     * precisa de um instante para aplicar e mandar a posicao nova).
     */
    public boolean isPositionCorrectionPending(long graceMs) {
        if (this.correctionsDelivered < this.correctionsSent.get()) {
            return true;
        }
        return System.currentTimeMillis() - this.lastCorrectionDeliveredAt < graceMs;
    }

    // --------------- cliente lento (NettySlowClientShedding)

    /**
     * Fila de envio acima do limite de descarte (NettySlowClientShedBytes) ou conexao cheia: o cliente esta
     * atrasado e pacotes visuais de outros objetos podem ser pulados.
     */
    public boolean isLagging() {
        return !this.channel.isWritable() || this.pendingWriteBytes() > Config.NETTY_SLOW_CLIENT_SHED_BYTES;
    }

    /** Algum pacote visual foi pulado: quando a fila esvaziar, ressincroniza. */
    public void markShed() {
        if (!this.shedPending) {
            this.shedPending = true;
            this.scheduleDrainCheck(DRAIN_CHECK_MS);
        }
    }

    /** A conexao voltou a aceitar dados (NettyGameDispatcher): confere ja se da para ressincronizar. */
    public void onWritableAgain() {
        if (this.shedPending) {
            this.scheduleDrainCheck(0L);
        }
    }

    /**
     * Confere a cada meio segundo se a fila esvaziou (abaixo da metade do limite de descarte). Quando esvazia,
     * manda o estado atual de quem esta em volta, no maximo a cada RESYNC_MIN_INTERVAL_MS.
     */
    private void scheduleDrainCheck(long delay) {
        if (!this.resyncScheduled.compareAndSet(false, true)) {
            return;
        }
        ThreadPoolManager.getInstance().schedule(() -> {
            this.resyncScheduled.set(false);
            if (!this.channel.isActive() || !this.shedPending) {
                return;
            }
            boolean drained = this.channel.isWritable() && this.pendingWriteBytes() <= Config.NETTY_SLOW_CLIENT_SHED_BYTES / 2;
            long sinceLast = System.currentTimeMillis() - this.lastResyncAt;
            if (!drained || sinceLast < RESYNC_MIN_INTERVAL_MS) {
                this.scheduleDrainCheck(drained ? RESYNC_MIN_INTERVAL_MS - sinceLast : DRAIN_CHECK_MS);
                return;
            }
            this.shedPending = false;
            this.lastResyncAt = System.currentTimeMillis();
            GameClient c = this.client;
            if (c != null) {
                c.resyncAfterSlowLink();
            }
        }, delay);
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