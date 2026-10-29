package l2.gameserver.network.l2.netty.disruptor;

import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DisruptorPipelineTest {

    @Test
    @DisplayName("Deve exigir que o tamanho do RingBuffer seja potência de 2")
    void testBufferSizeMustBePowerOfTwo() {
        assertThatThrownBy(() -> new DisruptorPipeline((conn, payload) -> {}, 1000))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("power of two");
    }

    @Test
    @DisplayName("Deve processar 1.000 pacotes concorrentes com entrega garantida e zero perda")
    void testThousandPacketsDispatched() throws Exception {
        int totalPackets = 1000;
        CountDownLatch latch = new CountDownLatch(totalPackets);
        AtomicInteger counter = new AtomicInteger();

        DisruptorPipeline pipeline = new DisruptorPipeline((conn, payload) -> {
            counter.incrementAndGet();
            latch.countDown();
        }, 1024);

        pipeline.start();

        EmbeddedChannel channel = new EmbeddedChannel();
        NettyGameConnection conn = new NettyGameConnection(channel);

        for (int i = 0; i < totalPackets; i++) {
            byte[] data = new byte[] { (byte) (i & 0xFF), 0x01, 0x02 };
            pipeline.publish(conn, data, data.length);
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        assertThat(counter.get()).isEqualTo(totalPackets);

        pipeline.shutdown();
    }

    @Test
    @DisplayName("Deve executar o warmup do compilador JIT C2 sem exceções")
    void testJitWarmup() {
        DisruptorPipeline pipeline = new DisruptorPipeline((conn, payload) -> {}, 1024);
        pipeline.start();

        // Executa 500 iterações de warmup
        pipeline.warmup(500);

        pipeline.shutdown();
    }
}
