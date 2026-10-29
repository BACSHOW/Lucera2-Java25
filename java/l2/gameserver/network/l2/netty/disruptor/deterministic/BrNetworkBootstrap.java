package l2.gameserver.network.l2.netty.disruptor.deterministic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import l2.gameserver.network.l2.netty.disruptor.topology.CpuTopologyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Orquestrador do pipeline de rede e simulação determinística a 120 Hz.
 * <p>
 * Mechanical Sympathy:
 * 1. Inicializa N {@link BrCoreWorker}s amarrados individualmente aos núcleos físicos elegíveis da topologia.
 * 2. Configura Netty com transporte nativo (Epoll / KQueue / NIO) com buffers agrupados (PooledByteBufAllocator).
 * 3. Agenda o {@link BrNettyOutboundPusher} para batch-flushing regular com ultra-baixa latência.
 */
public final class BrNetworkBootstrap {
    private static final Logger _log = LoggerFactory.getLogger(BrNetworkBootstrap.class);

    private final CpuTopologyConfig topologyConfig;
    private final BrTickSimulationHandler simulationHandler;
    private final BrCoreWorker[] workers;
    private final BrNettyInboundHandler inboundHandler;
    private final BrNettyOutboundPusher outboundPusher;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ScheduledExecutorService pusherExecutor;
    private ChannelFuture serverChannelFuture;
    private boolean started = false;

    public BrNetworkBootstrap(CpuTopologyConfig topologyConfig, BrTickSimulationHandler simulationHandler) {
        this.topologyConfig = topologyConfig;
        this.simulationHandler = simulationHandler;

        int count = topologyConfig.workerCount();
        int mask = topologyConfig.workerBitMask();
        int[] coreIds = topologyConfig.workerCoreIds();

        this.workers = new BrCoreWorker[count];
        for (int i = 0; i < count; i++) {
            this.workers[i] = new BrCoreWorker(i, coreIds[i], simulationHandler);
        }

        this.inboundHandler = new BrNettyInboundHandler(this.workers, mask);
        this.outboundPusher = new BrNettyOutboundPusher(this.workers);
    }

    public synchronized void start(int port) throws InterruptedException {
        if (this.started) {
            return;
        }

        _log.info("[BrNetworkBootstrap] Inicializando {} workers determinísticos a 120 Hz...", this.workers.length);
        for (BrCoreWorker worker : this.workers) {
            worker.start();
        }

        // Loop de I/O em lote para drenagem de pacotes de saída
        this.pusherExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "BrNettyOutboundPusher");
            t.setDaemon(true);
            t.setPriority(Thread.MAX_PRIORITY);
            return t;
        });
        this.pusherExecutor.scheduleAtFixedRate(this.outboundPusher, 1, 1, TimeUnit.MILLISECONDS);

        // Seleção de transporte nativo de rede
        Class<? extends io.netty.channel.ServerChannel> channelClass;
        if (Epoll.isAvailable()) {
            this.bossGroup = new EpollEventLoopGroup(1);
            this.workerGroup = new EpollEventLoopGroup(2);
            channelClass = EpollServerSocketChannel.class;
            _log.info("[BrNetworkBootstrap] Transporte de rede: Epoll nativo (Linux)");
        } else if (KQueue.isAvailable()) {
            this.bossGroup = new KQueueEventLoopGroup(1);
            this.workerGroup = new KQueueEventLoopGroup(2);
            channelClass = KQueueServerSocketChannel.class;
            _log.info("[BrNetworkBootstrap] Transporte de rede: KQueue nativo (macOS Darwin)");
        } else {
            this.bossGroup = new NioEventLoopGroup(1);
            this.workerGroup = new NioEventLoopGroup(2);
            channelClass = NioServerSocketChannel.class;
            _log.info("[BrNetworkBootstrap] Transporte de rede: NIO Java Padrão");
        }

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.bossGroup, this.workerGroup)
            .channel(channelClass)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .option(ChannelOption.SO_REUSEADDR, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(inboundHandler);
                }
            });

        this.serverChannelFuture = bootstrap.bind(new InetSocketAddress(port)).sync();
        this.started = true;
        _log.info("[BrNetworkBootstrap] Servidor ouvindo na porta {} com {} workers determinísticos.", port, this.workers.length);
    }

    public synchronized void shutdown() {
        if (!this.started) {
            return;
        }

        _log.info("[BrNetworkBootstrap] Encerrando pipeline determinístico...");
        if (this.serverChannelFuture != null) {
            this.serverChannelFuture.channel().close();
        }
        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
        if (this.pusherExecutor != null) {
            this.pusherExecutor.shutdown();
        }
        for (BrCoreWorker worker : this.workers) {
            worker.stop();
        }
        this.started = false;
        _log.info("[BrNetworkBootstrap] Pipeline finalizado com sucesso.");
    }

    public BrCoreWorker[] getWorkers() {
        return this.workers.clone();
    }

    public BrNettyInboundHandler getInboundHandler() {
        return this.inboundHandler;
    }

    public BrNettyOutboundPusher getOutboundPusher() {
        return this.outboundPusher;
    }
}
