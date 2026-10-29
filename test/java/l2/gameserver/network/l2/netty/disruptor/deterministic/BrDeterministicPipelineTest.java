package l2.gameserver.network.l2.netty.disruptor.deterministic;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class BrDeterministicPipelineTest {

    @Test
    @DisplayName("Layout de Frame Off-Heap: codificação e decodificação estrita sem alocação no heap")
    void testOffHeapFrameHeader() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(128);
        UnsafeBuffer buffer = new UnsafeBuffer(byteBuffer);

        int sessionId = 1001;
        int sequenceId = 42;
        int payloadLen = 8;

        BrFrameHeader.encodeHeader(buffer, 0, sessionId, sequenceId, payloadLen);

        // Grava payload de teste
        buffer.putLong(BrFrameHeader.PAYLOAD_OFFSET, 0x1122334455667788L);

        assertThat(BrFrameHeader.readSessionId(buffer, 0)).isEqualTo(1001);
        assertThat(BrFrameHeader.readSequenceId(buffer, 0)).isEqualTo(42);
        assertThat(BrFrameHeader.readPayloadLength(buffer, 0)).isEqualTo(8);
        assertThat(buffer.getLong(BrFrameHeader.PAYLOAD_OFFSET)).isEqualTo(0x1122334455667788L);
    }

    @Test
    @DisplayName("BrCoreWorker: Execução determinística de tick a 120 Hz com ordem FIFO preservada")
    void testCoreWorker120HzExecution() throws InterruptedException {
        int totalPackets = 1000;
        CountDownLatch latch = new CountDownLatch(totalPackets);
        List<Integer> receivedSequences = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger tickCount = new AtomicInteger(0);

        BrTickSimulationHandler handler = new BrTickSimulationHandler() {
            @Override
            public void onInboundPacket(int sessionId, int sequenceId, org.agrona.DirectBuffer buffer, int payloadOffset, int payloadLength) {
                receivedSequences.add(sequenceId);
                latch.countDown();
            }

            @Override
            public void onTick(long tickId, long nanoTime, BrCoreWorker worker) {
                tickCount.incrementAndGet();
            }
        };

        BrCoreWorker worker = new BrCoreWorker(0, 0, handler, 512 * 1024);
        worker.start();

        ByteBuffer rawBuffer = ByteBuffer.allocateDirect(16);
        UnsafeBuffer payload = new UnsafeBuffer(rawBuffer);
        payload.putLong(0, 99999L);

        // Publica 1.000 pacotes em ordem estrita de sequência
        for (int i = 1; i <= totalPackets; i++) {
            boolean written = worker.writeInbound(100, i, payload, 0, 8);
            assertThat(written).as("Ring buffer não pode rejeitar escrita sob carga moderada").isTrue();
        }

        // Aguarda a drenagem dos pacotes pelo worker
        boolean drained = latch.await(2, TimeUnit.SECONDS);
        assertThat(drained).as("Todos os pacotes devem ser drenados na janela de tick").isTrue();

        // Verifica preservação estrita de ordem FIFO (1, 2, 3, ... 1000)
        assertThat(receivedSequences).hasSize(totalPackets);
        for (int i = 0; i < totalPackets; i++) {
            assertThat(receivedSequences.get(i)).isEqualTo(i + 1);
        }

        // Aguarda alguns ticks adicionais para testar a cadência de 120 Hz
        Thread.sleep(100); // 100ms devem conter ~12 ticks (120 Hz = 1 tick a cada 8.33ms)
        worker.stop();

        assertThat(tickCount.get()).isGreaterThanOrEqualTo(10);
    }
}
