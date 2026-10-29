package l2.gameserver.network.l2.netty.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.netty.channel.Channel;
import io.netty.buffer.Unpooled;
import l2.gameserver.network.l2.netty.NettyGameConnection;

import java.util.HashMap;
import java.util.Map;

/** Coalesces high-frequency visual updates into one outbound flush per tick. */
public final class OutboundFloodQueue {
    public enum Kind { DELETE_OBJECT, CHAR_INFO, NPC_INFO, SKILL_LIST, ETC_STATUS }

    private static final long FLUSH_NANOS = 25_000_000L;

    private static final class Event {
        NettyGameConnection connection;
        byte[] payload;
        Kind kind;
        int objectId;
        void set(NettyGameConnection c, byte[] p, Kind k, int id) { connection = c; payload = p; kind = k; objectId = id; }
        void clear() { connection = null; payload = null; kind = null; objectId = 0; }
    }

    private final NettyGameConnection connection;

    private static final class Shared {
        static final Disruptor<Event> DISRUPTOR = create();
        static final RingBuffer<Event> RING = DISRUPTOR.getRingBuffer();
        private static Disruptor<Event> create() {
            Disruptor<Event> d = new Disruptor<>(Event::new, 65536, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new SleepingWaitStrategy());
            d.handleEventsWith((event, sequence, endOfBatch) -> {
                NettyGameConnection c = event.connection;
                byte[] payload = event.payload;
                Kind kind = event.kind;
                int objectId = event.objectId;
                if (c != null && c.isConnected()) c.getChannel().eventLoop().execute(() -> c.acceptFloodEvent(payload, kind, objectId));
                event.clear();
            });
            d.start();
            return d;
        }
    }

    public OutboundFloodQueue(NettyGameConnection connection, int bufferSize) {
        if (bufferSize <= 0 || (bufferSize & (bufferSize - 1)) != 0) throw new IllegalArgumentException("bufferSize must be a power of two");
        this.connection = connection;
        Shared.RING.getBufferSize();
    }

    public void publish(byte[] payload, Kind kind, int objectId) {
        if (payload == null || payload.length == 0 || !connection.isConnected()) return;
        long sequence;
        try {
            sequence = Shared.RING.tryNext();
        } catch (InsufficientCapacityException ignored) {
            return;
        }
        try { Shared.RING.get(sequence).set(connection, payload, kind, objectId); }
        finally { Shared.RING.publish(sequence); }
    }

    public void shutdown() { }

    public static final class State {
        private static final int MAX_PENDING_MESSAGES = 256;
        private final Map<Long, byte[]> latest = new HashMap<>();
        private long scheduledAt;
        public void accept(Channel channel, byte[] payload, Kind kind, int objectId) {
            latest.put(key(kind, objectId), payload);
            if (latest.size() >= MAX_PENDING_MESSAGES) {
                flush(channel);
                return;
            }
            long now = System.nanoTime();
            if (scheduledAt == 0L) {
                scheduledAt = now;
                channel.eventLoop().schedule(() -> flush(channel), FLUSH_NANOS, java.util.concurrent.TimeUnit.NANOSECONDS);
            }
        }
        private void flush(Channel channel) {
            scheduledAt = 0L;
            for (byte[] payload : latest.values()) channel.write(Unpooled.wrappedBuffer(payload));
            latest.clear();
            if (channel.isActive()) channel.flush();
        }
        private static long key(Kind kind, int objectId) { return (((long) kind.ordinal()) << 32) ^ (objectId & 0xffffffffL); }
    }
}
