package l2.gameserver.network.l2.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.function.Function;

/**
 * Auto-detecta o melhor transporte nativo do Netty para o sistema operacional atual:
 * - Linux: Epoll (chamadas de sistema direta ao kernel, zero-copy, SO_REUSEPORT)
 * - macOS/BSD: KQueue (suporte a kqueue(2) nativo do Darwin)
 * - Fallback: NIO (portável, padrão java.nio Selector em qualquer JVM)
 */
public final class NettyNativeTransport {
    private static final Logger _log = LoggerFactory.getLogger(NettyNativeTransport.class);

    private final String name;
    private final Class<? extends ServerChannel> serverChannelClass;
    private final Function<Integer, EventLoopGroup> eventLoopFactory;

    public NettyNativeTransport(String name, Class<? extends ServerChannel> serverChannelClass, Function<Integer, EventLoopGroup> eventLoopFactory) {
        this.name = name;
        this.serverChannelClass = serverChannelClass;
        this.eventLoopFactory = eventLoopFactory;
    }

    public String getName() {
        return this.name;
    }

    public Class<? extends ServerChannel> getServerChannelClass() {
        return this.serverChannelClass;
    }

    public EventLoopGroup createBossGroup(int threads) {
        return this.eventLoopFactory.apply(threads);
    }

    public EventLoopGroup createWorkerGroup(int threads) {
        return this.eventLoopFactory.apply(threads);
    }

    public static NettyNativeTransport detect() {
        NettyNativeTransport epoll = detectNative(
            "io.netty.channel.epoll.Epoll",
            "io.netty.channel.epoll.EpollEventLoopGroup",
            "io.netty.channel.epoll.EpollServerSocketChannel",
            "Epoll (Linux)",
            "epoll"
        );
        if (epoll != null) {
            return epoll;
        }

        NettyNativeTransport kqueue = detectNative(
            "io.netty.channel.kqueue.KQueue",
            "io.netty.channel.kqueue.KQueueEventLoopGroup",
            "io.netty.channel.kqueue.KQueueServerSocketChannel",
            "KQueue (macOS/BSD)",
            "kqueue"
        );
        if (kqueue != null) {
            return kqueue;
        }

        _log.info("[Netty] Native transport: NIO (portable fallback)");
        return new NettyNativeTransport(
            "nio",
            NioServerSocketChannel.class,
            threads -> new NioEventLoopGroup(threads)
        );
    }

    @SuppressWarnings("unchecked")
    private static NettyNativeTransport detectNative(
        String availabilityClass,
        String groupClass,
        String serverChannel,
        String label,
        String shortName
    ) {
        try {
            Class<?> availClazz = Class.forName(availabilityClass);
            boolean available = (boolean) availClazz.getMethod("isAvailable").invoke(null);
            if (!available) {
                return null;
            }
            Class<?> groupClazz = Class.forName(groupClass);
            Class<? extends ServerChannel> channelClazz = (Class<? extends ServerChannel>) Class.forName(serverChannel);
            Constructor<?> ctor = groupClazz.getConstructor(int.class);
            _log.debug("[Netty] Native transport detected: " + label);
            return new NettyNativeTransport(
                shortName,
                channelClazz,
                threads -> {
                    try {
                        return (EventLoopGroup) ctor.newInstance(threads);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate " + groupClass, e);
                    }
                }
            );
        } catch (Throwable ignored) {
            return null;
        }
    }
}
