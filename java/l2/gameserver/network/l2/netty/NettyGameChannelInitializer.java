package l2.gameserver.network.l2.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import l2.gameserver.network.l2.GamePacketHandler;
import l2.gameserver.network.l2.netty.codec.L2FrameDecoder;
import l2.gameserver.network.l2.netty.codec.L2PacketDecoder;
import l2.gameserver.network.l2.netty.codec.L2PacketEncoder;
import l2.gameserver.network.l2.netty.codec.NettyHaProxyHandler;
import l2.gameserver.network.l2.netty.disruptor.DisruptorPipeline;

/**
 * Configura o pipeline Netty para cada conexão de cliente do Lineage II.
 */
public final class NettyGameChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final GamePacketHandler packetHandler;
    private final boolean isHaProxy;
    private final DisruptorPipeline disruptorPipeline;

    public NettyGameChannelInitializer(GamePacketHandler packetHandler, boolean isHaProxy) {
        this(packetHandler, isHaProxy, null);
    }

    public NettyGameChannelInitializer(GamePacketHandler packetHandler, boolean isHaProxy, DisruptorPipeline disruptorPipeline) {
        this.packetHandler = packetHandler;
        this.isHaProxy = isHaProxy;
        this.disruptorPipeline = disruptorPipeline;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        if (this.isHaProxy) {
            pipeline.addLast("haProxyDetector", new NettyHaProxyHandler());
        }

        pipeline.addLast("frameDecoder", new L2FrameDecoder());
        pipeline.addLast("packetDecoder", new L2PacketDecoder());
        pipeline.addLast("packetEncoder", new L2PacketEncoder());
        pipeline.addLast("gameDispatcher", new NettyGameDispatcher(this.packetHandler, this.disruptorPipeline));
    }
}
