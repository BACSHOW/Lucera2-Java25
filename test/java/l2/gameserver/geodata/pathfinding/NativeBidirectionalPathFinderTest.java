package l2.gameserver.geodata.pathfinding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class NativeBidirectionalPathFinderTest {

    @Test
    @DisplayName("Poda cardinal de duas células cobra os dois passos")
    void testJumpMovementCost() {
        assertThat(NativeBidirectionalPathFinder.movementCost(0, 1))
            .isEqualTo(NativeBidirectionalPathFinder.MOVE_COST_CARDINAL);
        assertThat(NativeBidirectionalPathFinder.movementCost(0, 2))
            .isEqualTo(NativeBidirectionalPathFinder.MOVE_COST_CARDINAL * 2);
        assertThat(NativeBidirectionalPathFinder.movementCost(4, 1))
            .isEqualTo(NativeBidirectionalPathFinder.MOVE_COST_DIAGONAL);
    }

    @Test
    @DisplayName("Empacotamento 64-bit Zero-GC: valida integridade de coordenadas reais do Lineage II e Z com sinal")
    void testCoordinatePacking() {
        int gx = 25000;
        int gy = 35000;
        int z = -3404;

        long packed = NativeBidirectionalPathFinder.pack(gx, gy, z);

        assertThat(NativeBidirectionalPathFinder.unpackGx(packed))
            .as("GeoX deve ser desempacotado com precisão")
            .isEqualTo(gx);

        assertThat(NativeBidirectionalPathFinder.unpackGy(packed))
            .as("GeoY deve ser desempacotado com precisão")
            .isEqualTo(gy);

        assertThat(NativeBidirectionalPathFinder.unpackZ(packed))
            .as("Z negativo deve ser preservado")
            .isEqualTo(z);
    }

    @Test
    @DisplayName("Serialização Cap'n Proto Wire Format: validação e leitura de waypoints compactos")
    void testPathTrajectoryBinary() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        int ox = 83400, oy = 147943, oz = -3404;
        int tx = 83500, ty = 148000, tz = -3400;
        float distance = 150.5f;

        int[] wayX = new int[] { 83420, 83460, 83500 };
        int[] wayY = new int[] { 147960, 147980, 148000 };
        int[] wayZ = new int[] { -3404, -3402, -3400 };

        PathTrajectoryBinary.serialize(buffer, ox, oy, oz, tx, ty, tz, distance, wayX, wayY, wayZ, 3);
        buffer.flip();

        assertThat(PathTrajectoryBinary.isValid(buffer))
            .as("Buffer serializado deve ser válido no formato PATH v1")
            .isTrue();

        assertThat(PathTrajectoryBinary.getWaypointCount(buffer))
            .as("Contagem de waypoints deve ser 3")
            .isEqualTo(3);

        assertThat(PathTrajectoryBinary.getTotalDistance(buffer))
            .as("Distância total calculada")
            .isEqualTo(distance);

        int[] outPoint = new int[3];
        PathTrajectoryBinary.readWaypoint(buffer, 1, outPoint);
        assertThat(outPoint[0]).isEqualTo(83460);
        assertThat(outPoint[1]).isEqualTo(147980);
        assertThat(outPoint[2]).isEqualTo(-3402);
    }

    @Test
    @DisplayName("Concorrência de Empacotamento e Serialização: 16 threads paralelas Zero-GC")
    void testConcurrentPackingAndSerialization() throws InterruptedException {
        int threads = 16;
        int iterations = 10_000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCounter = new AtomicInteger(0);

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    ByteBuffer localBuffer = ByteBuffer.allocate(256);
                    int[] wx = new int[] { 100, 200 };
                    int[] wy = new int[] { 300, 400 };
                    int[] wz = new int[] { -1000, -1000 };

                    for (int i = 0; i < iterations; i++) {
                        int gx = 10000 + (threadId * 100) + (i % 50);
                        int gy = 20000 + (threadId * 100);
                        int z = -3000 + (i % 100);

                        long packed = NativeBidirectionalPathFinder.pack(gx, gy, z);
                        if (NativeBidirectionalPathFinder.unpackGx(packed) == gx &&
                            NativeBidirectionalPathFinder.unpackGy(packed) == gy &&
                            NativeBidirectionalPathFinder.unpackZ(packed) == z) {

                            localBuffer.clear();
                            PathTrajectoryBinary.serialize(localBuffer, gx, gy, z, gx + 10, gy + 10, z, 14.0f, wx, wy, wz, 2);
                            localBuffer.flip();
                            if (PathTrajectoryBinary.isValid(localBuffer)) {
                                successCounter.incrementAndGet();
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(successCounter.get())
            .as("160.000 operações concorrentes executadas com sucesso sem corrupção")
            .isEqualTo(threads * iterations);
    }
}
