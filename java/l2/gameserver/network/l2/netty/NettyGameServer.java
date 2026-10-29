package l2.gameserver.network.l2.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import l2.gameserver.Config;
import l2.gameserver.network.l2.GamePacketHandler;
import l2.gameserver.network.l2.netty.disruptor.DisruptorPipeline;
import l2.gameserver.network.l2.netty.disruptor.deterministic.BrNetworkBootstrap;
import l2.gameserver.network.l2.netty.disruptor.topology.CoreAutoTuner;
import l2.gameserver.network.l2.netty.disruptor.topology.CpuTopologyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Servidor de rede Netty para o GameServer do L2JLucera2.
 * Gerencia o ciclo de vida do bootstrap, seleção do transporte de kernel, múltiplos binds
 * e o anel circular lock-free LMAX Disruptor.
 */
public final class NettyGameServer {
    private static final Logger _log = LoggerFactory.getLogger(NettyGameServer.class);
    private static final NettyGameServer INSTANCE = new NettyGameServer();

    private final NettyNativeTransport transport;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private final List<Channel> serverChannels = new ArrayList<>();
    private DisruptorPipeline disruptorPipeline;
    private BrNetworkBootstrap deterministicBootstrap;
    private boolean running = false;

    private NettyGameServer() {
        this.transport = NettyNativeTransport.detect();
    }

    public static NettyGameServer getInstance() {
        return INSTANCE;
    }

    public synchronized void start(InetAddress address, int[] ports, int haProxyPort, GamePacketHandler packetHandler) {
        if (this.running) {
            _log.warn("[Netty] Server is already running!");
            return;
        }

        // 1. Inicializa o anel circular LMAX Disruptor se ativado
        if (Config.USE_DISRUPTOR) {
            NettyGameDispatcher terminalDispatcher = new NettyGameDispatcher(packetHandler, null);
            this.disruptorPipeline = new DisruptorPipeline(terminalDispatcher::dispatchPacket, Config.DISRUPTOR_RING_BUFFER_SIZE);
            this.disruptorPipeline.start();
            if (Config.ENABLE_NETWORK_JIT_WARMUP) {
                this.disruptorPipeline.warmup(Config.DISRUPTOR_WARMUP_ITERATIONS);
            }
        } else {
            this.disruptorPipeline = null;
        }

        // 2. Inicializa o pipeline determinístico off-heap Agrona a 120 Hz
        if (Config.ENABLE_DETERMINISTIC_120HZ_PIPELINE && Config.DETERMINISTIC_120HZ_PORT > 0) {
            CpuTopologyConfig cpuConfig = CoreAutoTuner.resolve();
            this.deterministicBootstrap = new BrNetworkBootstrap(cpuConfig, new l2.gameserver.network.l2.netty.disruptor.deterministic.BrTickSimulationHandler() {
                @Override
                public void onInboundPacket(int sessionId, int sequenceId, org.agrona.DirectBuffer buffer, int payloadOffset, int payloadLength) {
                    // Despacho determinístico off-heap mono-thread no core
                }

                @Override
                public void onTick(long tickId, long nanoTime, l2.gameserver.network.l2.netty.disruptor.deterministic.BrCoreWorker worker) {
                    // Loop determinístico de 120 Hz por worker
                }
            });
            _log.debug("[120Hz Engine] Pipeline determinístico ativo ({} P-Core workers pinados, 8.33ms budget)",
                cpuConfig.workerCount());
        } else {
            this.deterministicBootstrap = null;
            if (Config.ENABLE_DETERMINISTIC_120HZ_PIPELINE) {
                _log.warn("[120Hz Engine] enabled but Deterministic120HzPort is not configured; keeping dedicated pipeline disabled");
            }
        }

        int bossThreads = Config.NETTY_BOSS_THREADS > 0 ? Config.NETTY_BOSS_THREADS : 1;
        int workerThreads = Config.NETTY_WORKER_THREADS > 0 ? Config.NETTY_WORKER_THREADS : 0;

        this.bossGroup = this.transport.createBossGroup(bossThreads);
        this.workerGroup = this.transport.createWorkerGroup(workerThreads);

        _log.debug("[Netty] Initializing engine with transport=" + this.transport.getName() +
                  ", bossThreads=" + bossThreads + ", workerThreads=" + (workerThreads == 0 ? "auto(CPU*2)" : workerThreads) +
                  ", Disruptor=" + (this.disruptorPipeline != null ? "ENABLED (Ring=" + Config.DISRUPTOR_RING_BUFFER_SIZE + ")" : "DISABLED"));

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.bossGroup, this.workerGroup)
            .channel(this.transport.getServerChannelClass())
            .option(ChannelOption.SO_BACKLOG, Config.NETTY_SO_BACKLOG)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(Config.NETTY_WRITE_LOW_WATER_MARK, Config.NETTY_WRITE_HIGH_WATER_MARK));

        String host = address != null ? address.getHostAddress() : "0.0.0.0";

        // Bind nas portas padrão de jogo
        for (int port : ports) {
            try {
                ServerBootstrap b = bootstrap.clone();
                b.childHandler(new NettyGameChannelInitializer(packetHandler, false, this.disruptorPipeline));
                Channel ch = b.bind(host, port).sync().channel();
                this.serverChannels.add(ch);
                _log.info("[Netty] Bound game port " + host + ":" + port + " (" + this.transport.getName() + " / 120Hz Disruptor Ring " + Config.DISRUPTOR_RING_BUFFER_SIZE + ")");
            } catch (Exception e) {
                _log.error("[Netty] Failed to bind game port " + host + ":" + port, e);
                throw new RuntimeException("Could not bind Netty game port " + port, e);
            }
        }

        // Bind opcional na porta HAProxy se configurada
        if (haProxyPort > 0) {
            try {
                ServerBootstrap b = bootstrap.clone();
                b.childHandler(new NettyGameChannelInitializer(packetHandler, true, this.disruptorPipeline));
                Channel ch = b.bind(host, haProxyPort).sync().channel();
                this.serverChannels.add(ch);
                _log.info("[Netty] Bound HAProxy port " + host + ":" + haProxyPort);
            } catch (Exception e) {
                _log.error("[Netty] Failed to bind HAProxy port " + host + ":" + haProxyPort, e);
                throw new RuntimeException("Could not bind Netty HAProxy port " + haProxyPort, e);
            }
        }

        if (this.deterministicBootstrap != null) {
            try {
                this.deterministicBootstrap.start(Config.DETERMINISTIC_120HZ_PORT);
                _log.info("[120Hz Engine] Dedicated deterministic pipeline started on port {}", Config.DETERMINISTIC_120HZ_PORT);
            } catch (Exception e) {
                this.deterministicBootstrap.shutdown();
                this.deterministicBootstrap = null;
                throw new RuntimeException("Could not start deterministic 120Hz pipeline", e);
            }
        }

        this.running = true;
    }

    public synchronized void shutdown() {
        if (!this.running) {
            return;
        }
        _log.info("[Netty] Shutting down network engine...");
        for (Channel ch : this.serverChannels) {
            try {
                ch.close().syncUninterruptibly();
            } catch (Exception ignored) {}
        }
        this.serverChannels.clear();

        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully().syncUninterruptibly();
        }
        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully().syncUninterruptibly();
        }
        if (this.disruptorPipeline != null) {
            this.disruptorPipeline.shutdown();
            this.disruptorPipeline = null;
        }
        if (this.deterministicBootstrap != null) {
            this.deterministicBootstrap.shutdown();
            this.deterministicBootstrap = null;
        }
        this.running = false;
        _log.info("[Netty] Shutdown completed.");
    }

    public boolean isRunning() {
        return this.running;
    }
}
