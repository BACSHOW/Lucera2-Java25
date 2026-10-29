package l2.gameserver.network.l2.netty.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pipeline do LMAX Disruptor para absorver picos de tráfego de entrada com zero alocação no heap.
 * Utiliza RingBuffer de 2^16 (65.536) slots e SleepingWaitStrategy para latência sub-milissegundo.
 */
public final class DisruptorPipeline {
    private static final Logger _log = LoggerFactory.getLogger(DisruptorPipeline.class);
    public static final int DEFAULT_BUFFER_SIZE = 65536; // 2^16

    private final Disruptor<PacketEvent> disruptor;
    private final RingBuffer<PacketEvent> ringBuffer;
    private final int bufferSize;
    private boolean started = false;

    public DisruptorPipeline(NettyPacketConsumer consumer) {
        this(consumer, DEFAULT_BUFFER_SIZE);
    }

    public DisruptorPipeline(NettyPacketConsumer consumer, int bufferSize) {
        if (bufferSize <= 0 || (bufferSize & (bufferSize - 1)) != 0) {
            throw new IllegalArgumentException("RingBuffer size must be a power of two, got " + bufferSize);
        }
        this.bufferSize = bufferSize;
        this.disruptor = new Disruptor<>(
            PacketEventFactory.INSTANCE,
            bufferSize,
            DaemonThreadFactory.INSTANCE,
            ProducerType.MULTI,
            new SleepingWaitStrategy()
        );

        InboundPacketHandler handler = new InboundPacketHandler(consumer);
        this.disruptor.handleEventsWith(handler);
        this.ringBuffer = this.disruptor.getRingBuffer();
    }

    public synchronized void start() {
        if (this.started) {
            return;
        }
        this.disruptor.start();
        this.started = true;
        _log.debug("[Disruptor] Inbound RingBuffer started (slots=" + this.bufferSize + ", MULTI, SleepingWaitStrategy)");
    }

    public void publish(NettyGameConnection connection, byte[] payload, int length) {
        long seq = this.ringBuffer.next();
        try {
            this.ringBuffer.get(seq).set(connection, payload, length);
        } finally {
            this.ringBuffer.publish(seq);
        }
    }

    /**
     * Warmup para mitigar o Cold-Start do JIT C2:
     * Publica eventos dummy antes da abertura das portas públicas para aquecer
     * o compilador Hotspot C2 e garantir residência em cache L2/L3.
     */
    public void warmup(int iterations) {
        if (!this.started || iterations <= 0) {
            return;
        }
        _log.debug("[Disruptor] Warming up JIT C2 compiler (" + iterations + " iterations)...");
        byte[] dummy = new byte[] { 0x00 };
        for (int i = 0; i < iterations; i++) {
            this.publish(null, dummy, 0);
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {}
        _log.debug("[Disruptor] JIT C2 warmup complete.");
    }

    public synchronized void shutdown() {
        if (!this.started) {
            return;
        }
        this.disruptor.shutdown();
        this.started = false;
        _log.info("[Disruptor] Inbound RingBuffer shut down.");
    }
}
