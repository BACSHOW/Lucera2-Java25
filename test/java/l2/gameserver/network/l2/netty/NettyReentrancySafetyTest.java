package l2.gameserver.network.l2.netty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class NettyReentrancySafetyTest {

    /**
     * Harness que valida a lógica do pool de buffers reentrantes com profundidade garantida e fallback.
     */
    static class ReentrantBufferPoolHarness {
        private static final int MAX_DEPTH = 4;
        private static final int BUFFER_SIZE = 1024;

        private final ThreadLocal<ByteBuffer[]> buffers = ThreadLocal.withInitial(() -> {
            ByteBuffer[] pool = new ByteBuffer[MAX_DEPTH];
            for (int i = 0; i < MAX_DEPTH; i++) {
                pool[i] = ByteBuffer.allocate(BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
            }
            return pool;
        });

        private final ThreadLocal<int[]> depth = ThreadLocal.withInitial(() -> new int[1]);
        final AtomicInteger overflowCounter = new AtomicInteger();

        public ByteBuffer acquire() {
            int[] d = depth.get();
            int currentDepth = d[0]++;
            if (currentDepth >= MAX_DEPTH) {
                overflowCounter.incrementAndGet();
                return ByteBuffer.allocate(BUFFER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
            }
            ByteBuffer buf = buffers.get()[currentDepth];
            buf.clear();
            return buf;
        }

        public void release() {
            int[] d = depth.get();
            if (d[0] > 0) {
                d[0]--;
            }
        }

        public int getCurrentDepth() {
            return depth.get()[0];
        }
    }

    @Test
    @DisplayName("Aquisições aninhadas até MAX_DEPTH usam slots de buffer separados")
    void nestedAcquiresUseSeparateSlots() {
        ReentrantBufferPoolHarness pool = new ReentrantBufferPoolHarness();
        ByteBuffer b0 = pool.acquire();
        try {
            assertEquals(1, pool.getCurrentDepth());
            ByteBuffer b1 = pool.acquire();
            try {
                assertEquals(2, pool.getCurrentDepth());
                assertNotSame(b0, b1);
            } finally {
                pool.release();
            }
            assertEquals(1, pool.getCurrentDepth());
        } finally {
            pool.release();
        }
        assertEquals(0, pool.getCurrentDepth());
    }

    @Test
    @DisplayName("Excesso de profundidade (> MAX_DEPTH) aloca fallback seguro sem estourar exceção")
    void overflowDepthAllocatesFallbackGracefully() {
        ReentrantBufferPoolHarness pool = new ReentrantBufferPoolHarness();
        ByteBuffer[] acquired = new ByteBuffer[6];
        try {
            for (int i = 0; i < 6; i++) {
                acquired[i] = pool.acquire();
            }
            assertEquals(6, pool.getCurrentDepth());
            assertEquals(2, pool.overflowCounter.get()); // 5º e 6º foram fallback
            assertNotNull(acquired[4]);
            assertNotNull(acquired[5]);
        } finally {
            for (int i = 0; i < 6; i++) {
                pool.release();
            }
        }
        assertEquals(0, pool.getCurrentDepth());
    }

    @Test
    @DisplayName("Exceção durante uso restaura o contador de profundidade")
    void exceptionSafetyRestoresDepth() {
        ReentrantBufferPoolHarness pool = new ReentrantBufferPoolHarness();
        try {
            pool.acquire();
            throw new RuntimeException("Simulated failure inside packet write");
        } catch (RuntimeException e) {
            pool.release();
        }
        assertEquals(0, pool.getCurrentDepth());
        // Próxima chamada deve reutilizar slot 0 sem derivação
        ByteBuffer next = pool.acquire();
        try {
            assertEquals(1, pool.getCurrentDepth());
        } finally {
            pool.release();
        }
        assertEquals(0, pool.getCurrentDepth());
    }
}
