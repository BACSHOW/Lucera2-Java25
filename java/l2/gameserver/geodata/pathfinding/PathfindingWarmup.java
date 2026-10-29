package l2.gameserver.geodata.pathfinding;

import l2.gameserver.geodata.GeoEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Módulo de Warmup para Compilação JIT C2 e Otimização PGO (Profile-Guided Optimization).
 * Satura e pré-aquece o pipeline de pathfinding e serialização de trajetórias no boot,
 * eliminando cold-start lag e garantindo compilação nativa ultra-rápida desde o primeiro milissegundo.
 */
public final class PathfindingWarmup {
    private static final Logger _log = LoggerFactory.getLogger(PathfindingWarmup.class);

    private PathfindingWarmup() {
        // Utilitário
    }

    /**
     * Executa iterações de aquecimento sobre trajetórias e serializações de teste.
     */
    public static void runWarmup(int iterations) {
        if (iterations <= 0) {
            return;
        }

        long start = System.currentTimeMillis();
        _log.info("[Pathfinding] Iniciando JIT C2 Warmup do motor NBA* ({} iterações)...", iterations);

        NativeBidirectionalPathFinder finder = NativeBidirectionalPathFinder.getInstance();
        ByteBuffer testBuffer = ByteBuffer.allocateDirect(1024);
        int[] wayX = new int[] { 83400, 83450, 83500 };
        int[] wayY = new int[] { 147943, 147960, 147980 };
        int[] wayZ = new int[] { -3404, -3404, -3404 };

        for (int i = 0; i < iterations; i++) {
            // 1. Testa empacotamento e desempacotamento de primitivos 64-bit
            long packed = NativeBidirectionalPathFinder.pack(100 + i, 200 + i, -3000);
            int gx = NativeBidirectionalPathFinder.unpackGx(packed);
            int gy = NativeBidirectionalPathFinder.unpackGy(packed);
            int gz = NativeBidirectionalPathFinder.unpackZ(packed);

            // 2. Testa serialização binária de trajetórias
            testBuffer.clear();
            PathTrajectoryBinary.serialize(
                testBuffer,
                gx, gy, gz,
                gx + 100, gy + 100, gz,
                150.0f,
                wayX, wayY, wayZ, 3
            );
            testBuffer.flip();
            PathTrajectoryBinary.isValid(testBuffer);

            // 3. Executa busca simulada local
            finder.findPath(83400, 147943, -3404, 83450, 147960, -3404, 0);
        }

        long duration = System.currentTimeMillis() - start;
        _log.info("[Pathfinding] JIT C2 Warmup concluído em {} ms.", duration);
    }
}
