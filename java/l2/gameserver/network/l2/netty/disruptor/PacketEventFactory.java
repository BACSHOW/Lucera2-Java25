package l2.gameserver.network.l2.netty.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Fábrica que instancia os slots de PacketEvent durante a criação do RingBuffer.
 */
public final class PacketEventFactory implements EventFactory<PacketEvent> {
    public static final PacketEventFactory INSTANCE = new PacketEventFactory();

    @Override
    public PacketEvent newInstance() {
        return new PacketEvent();
    }
}
