package l2.gameserver.network.l2.netty.disruptor;

import l2.gameserver.network.l2.netty.NettyGameConnection;

/**
 * Consumidor dos pacotes descarregados do Disruptor RingBuffer.
 */
@FunctionalInterface
public interface NettyPacketConsumer {
    void onPacket(NettyGameConnection connection, byte[] payload);
}
