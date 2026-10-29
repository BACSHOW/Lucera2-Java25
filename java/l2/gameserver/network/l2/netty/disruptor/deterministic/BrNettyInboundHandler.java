package l2.gameserver.network.l2.netty.disruptor.deterministic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Roteador Inbound do Netty para o pipeline determinístico off-heap (Zero-GC).
 * <p>
 * Mechanical Sympathy:
 * 1. <b>Roteamento Determinístico Lock-Free</b>: Calcula {@code sessionId & workerBitMask} em O(1).
 * 2. <b>Isolamento de Estado (Thread-Affinity)</b>: Uma mesma sessão sempre é despachada para o mesmo worker,
 *    garantindo que o estado de um jogador nunca sofra concorrência entre threads.
 * 3. <b>Zero Alocações no Heap</b>: Bytes são transferidos do {@link ByteBuf} diretamente para o
 *    RingBuffer off-heap da Agrona, liberando o buffer do pool do Netty na saída.
 */
@ChannelHandler.Sharable
public final class BrNettyInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger _log = LoggerFactory.getLogger(BrNettyInboundHandler.class);

    public static final AttributeKey<Integer> ATTR_SESSION_ID = AttributeKey.valueOf("BR_SESSION_ID");
    public static final AttributeKey<AtomicInteger> ATTR_SEQUENCE_COUNTER = AttributeKey.valueOf("BR_SEQUENCE_COUNTER");

    private final BrCoreWorker[] workers;
    private final int workerBitMask;
    private final AtomicInteger sessionSequenceGenerator = new AtomicInteger(1);

    public BrNettyInboundHandler(BrCoreWorker[] workers, int workerBitMask) {
        this.workers = workers;
        this.workerBitMask = workerBitMask;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int sessionId = this.sessionSequenceGenerator.getAndIncrement();
        ctx.channel().attr(ATTR_SESSION_ID).set(sessionId);
        ctx.channel().attr(ATTR_SEQUENCE_COUNTER).set(new AtomicInteger(0));
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        int readableBytes = msg.readableBytes();
        if (readableBytes <= 0) {
            return;
        }

        Integer sessionId = ctx.channel().attr(ATTR_SESSION_ID).get();
        if (sessionId == null) {
            sessionId = ctx.channel().id().asShortText().hashCode() & 0x7FFFFFFF;
            ctx.channel().attr(ATTR_SESSION_ID).set(sessionId);
            ctx.channel().attr(ATTR_SEQUENCE_COUNTER).set(new AtomicInteger(0));
        }

        AtomicInteger seqCounter = ctx.channel().attr(ATTR_SEQUENCE_COUNTER).get();
        int sequenceId = seqCounter != null ? seqCounter.incrementAndGet() : 0;

        // Roteamento O(1) com máscara de bits
        int workerIndex = (sessionId & 0x7FFFFFFF) & this.workerBitMask;
        BrCoreWorker targetWorker = this.workers[workerIndex];

        // Transferência direta Zero-GC do ByteBuf do Netty para a Agrona
        UnsafeBuffer wrapperBuffer;
        int readIndex;

        if (msg.isDirect() && msg.hasMemoryAddress()) {
            wrapperBuffer = new UnsafeBuffer();
            wrapperBuffer.wrap(msg.memoryAddress() + msg.readerIndex(), readableBytes);
            readIndex = 0;
        } else if (msg.nioBufferCount() > 0) {
            ByteBuffer nioBuf = msg.internalNioBuffer(msg.readerIndex(), readableBytes);
            wrapperBuffer = new UnsafeBuffer(nioBuf);
            readIndex = nioBuf.position();
        } else {
            // Fallback para heap array se o ByteBuf for não-contíguo
            byte[] heapBytes = new byte[readableBytes];
            msg.getBytes(msg.readerIndex(), heapBytes);
            wrapperBuffer = new UnsafeBuffer(heapBytes);
            readIndex = 0;
        }

        boolean written = targetWorker.writeInbound(sessionId, sequenceId, wrapperBuffer, readIndex, readableBytes);
        if (!written) {
            _log.warn("[BrNettyInbound] RingBuffer do Worker #{} saturado! Descartando pacote (backpressure ativa) para sessão {}",
                workerIndex, sessionId);
        }
    }
}
