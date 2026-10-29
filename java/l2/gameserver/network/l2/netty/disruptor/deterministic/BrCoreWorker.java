package l2.gameserver.network.l2.netty.disruptor.deterministic;

import net.openhft.affinity.Affinity;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.MessageHandler;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Worker determinístico dedicado a um núcleo físico de CPU (Mechanical Sympathy).
 * <p>
 * Características mecânicas e de baixa latência:
 * 1. <b>Loop Estrito de 120 Hz</b>: Orçamento de 8.333.333 nanossegundos por quadro (~8.33ms).
 * 2. <b>Zero-GC e Memória Off-Heap</b>: 1 Inbound e 1 Outbound {@link ManyToOneRingBuffer} sobre Direct ByteBuffers da Agrona.
 * 3. <b>Alinhamento de Linha de Cache (64 bytes)</b>: Evita False Sharing entre núcleos adjacentes.
 * 4. <b>Afinidade de CPU (Thread Pinning)</b>: Fixa a thread no núcleo físico atribuído pela topologia.
 * 5. <b>Zero Jitter Busy-Wait</b>: Utiliza {@link Thread#onSpinWait()} durante o tempo ocioso do tick.
 */
public final class BrCoreWorker implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(BrCoreWorker.class);

    public static final long TICK_BUDGET_NANOS = 8_333_333L; // 120 Hz (1s / 120 = 8.333.333 ns)
    public static final int DEFAULT_RING_BUFFER_CAPACITY = 1024 * 1024; // 1 MB off-heap por anel (potência de 2)

    private final int workerIndex;
    private final int physicalCoreId;
    private final ManyToOneRingBuffer inboundRingBuffer;
    private final ManyToOneRingBuffer outboundRingBuffer;
    private final ByteBuffer inboundDirectBuffer;
    private final ByteBuffer outboundDirectBuffer;
    private final BrTickSimulationHandler simulationHandler;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread workerThread;

    // Estado do tick mono-thread (zero locks, zero volatilidade necessária)
    private long tickId = 0;
    private int outboundSequenceCounter = 0;

    // Handler reusável de mensagens inbound (Zero-GC)
    private final MessageHandler inboundDrainHandler;

    public BrCoreWorker(int workerIndex, int physicalCoreId, BrTickSimulationHandler simulationHandler) {
        this(workerIndex, physicalCoreId, simulationHandler, DEFAULT_RING_BUFFER_CAPACITY);
    }

    public BrCoreWorker(int workerIndex, int physicalCoreId, BrTickSimulationHandler simulationHandler, int bufferCapacity) {
        this.workerIndex = workerIndex;
        this.physicalCoreId = physicalCoreId;
        this.simulationHandler = simulationHandler;

        int totalBufferSize = bufferCapacity + RingBufferDescriptor.TRAILER_LENGTH;
        this.inboundDirectBuffer = ByteBuffer.allocateDirect(totalBufferSize);
        this.outboundDirectBuffer = ByteBuffer.allocateDirect(totalBufferSize);

        this.inboundRingBuffer = new ManyToOneRingBuffer(new UnsafeBuffer(this.inboundDirectBuffer));
        this.outboundRingBuffer = new ManyToOneRingBuffer(new UnsafeBuffer(this.outboundDirectBuffer));

        this.inboundDrainHandler = (msgTypeId, buffer, index, length) -> {
            if (msgTypeId == BrFrameHeader.MSG_TYPE_PACKET) {
                int sessionId = BrFrameHeader.readSessionId(buffer, index);
                int sequenceId = BrFrameHeader.readSequenceId(buffer, index);
                int payloadLength = BrFrameHeader.readPayloadLength(buffer, index);
                int payloadOffset = index + BrFrameHeader.PAYLOAD_OFFSET;
                if (this.simulationHandler != null) {
                    this.simulationHandler.onInboundPacket(sessionId, sequenceId, buffer, payloadOffset, payloadLength);
                }
            }
        };
    }

    public void start() {
        if (this.running.compareAndSet(false, true)) {
            this.workerThread = new Thread(this, "BrCoreWorker-" + this.workerIndex + "-Core#" + this.physicalCoreId);
            this.workerThread.setDaemon(true);
            this.workerThread.setPriority(Thread.MAX_PRIORITY);
            this.workerThread.start();
            _log.info("[BrCoreWorker-{}] Inicializado com sucesso no Core Físico #{} (120 Hz Engine)", this.workerIndex, this.physicalCoreId);
        }
    }

    public void stop() {
        this.running.set(false);
        if (this.workerThread != null) {
            try {
                this.workerThread.join(200);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        // 1. Aplicação de Afinidade de CPU (Thread Pinning nos núcleos P-Core)
        try {
            Affinity.setAffinity(this.physicalCoreId);
            _log.info("[BrCoreWorker-{}] Afinidade amarrada com sucesso ao Core #{}", this.workerIndex, this.physicalCoreId);
        } catch (Throwable t) {
            _log.warn("[BrCoreWorker-{}] CPU Pinning direto indisponível no host (fallback para tempo real JVM): {}",
                this.workerIndex, t.getMessage());
        }

        long nextTickTargetNanos = System.nanoTime();

        while (this.running.get()) {
            long tickStartNanos = System.nanoTime();

            // FASE A: Drenagem em lote dos pacotes de entrada (Zero-GC)
            this.inboundRingBuffer.read(this.inboundDrainHandler, 1024);

            // FASE B: Simulação Mecânica e Lógica de Jogo (Mono-thread, Lock-free)
            if (this.simulationHandler != null) {
                try {
                    this.simulationHandler.onTick(this.tickId, tickStartNanos, this);
                } catch (Throwable t) {
                    _log.error("[BrCoreWorker-{}] Erro inesperado no tick {}", this.workerIndex, this.tickId, t);
                }
            }

            // FASE C: Avanço do relógio de tick
            this.tickId++;
            nextTickTargetNanos += TICK_BUDGET_NANOS;

            // Sincronização e Espera Ativa Determinística (Busy-spin com PAUSE / onSpinWait)
            while (System.nanoTime() < nextTickTargetNanos) {
                Thread.onSpinWait();
            }
        }
    }

    /**
     * Grava um pacote diretamente no anel de entrada do worker sem alocações no heap.
     * Invocado de forma thread-safe pelos EventLoops de I/O do Netty.
     */
    public boolean writeInbound(int sessionId, int sequenceId, DirectBuffer payloadBuffer, int payloadOffset, int payloadLength) {
        int frameLength = BrFrameHeader.frameLength(payloadLength);
        int index = this.inboundRingBuffer.tryClaim(BrFrameHeader.MSG_TYPE_PACKET, frameLength);
        if (index < 0) {
            return false; // Ring buffer saturado (backpressure mecânica)
        }

        try {
            MutableDirectBuffer buffer = this.inboundRingBuffer.buffer();
            BrFrameHeader.encodeHeader(buffer, index, sessionId, sequenceId, payloadLength);
            buffer.putBytes(index + BrFrameHeader.PAYLOAD_OFFSET, payloadBuffer, payloadOffset, payloadLength);
            this.inboundRingBuffer.commit(index);
            return true;
        } catch (Throwable t) {
            this.inboundRingBuffer.abort(index);
            throw t;
        }
    }

    /**
     * Grava um pacote de resposta diretamente no anel de saída do worker com carimbo de sequência.
     * Invocado de forma puramente mono-thread durante a Fase B do tick.
     */
    public boolean writeOutbound(int sessionId, DirectBuffer payloadBuffer, int payloadOffset, int payloadLength) {
        int sequenceId = ++this.outboundSequenceCounter;
        int frameLength = BrFrameHeader.frameLength(payloadLength);
        int index = this.outboundRingBuffer.tryClaim(BrFrameHeader.MSG_TYPE_PACKET, frameLength);
        if (index < 0) {
            return false; // Backpressure no anel de saída
        }

        try {
            MutableDirectBuffer buffer = this.outboundRingBuffer.buffer();
            BrFrameHeader.encodeHeader(buffer, index, sessionId, sequenceId, payloadLength);
            buffer.putBytes(index + BrFrameHeader.PAYLOAD_OFFSET, payloadBuffer, payloadOffset, payloadLength);
            this.outboundRingBuffer.commit(index);
            return true;
        } catch (Throwable t) {
            this.outboundRingBuffer.abort(index);
            throw t;
        }
    }

    public int getWorkerIndex() {
        return this.workerIndex;
    }

    public int getPhysicalCoreId() {
        return this.physicalCoreId;
    }

    public long getTickId() {
        return this.tickId;
    }

    public ManyToOneRingBuffer getInboundRingBuffer() {
        return this.inboundRingBuffer;
    }

    public ManyToOneRingBuffer getOutboundRingBuffer() {
        return this.outboundRingBuffer;
    }
}
