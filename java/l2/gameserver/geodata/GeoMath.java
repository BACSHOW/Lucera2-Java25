package l2.gameserver.geodata;

/**
 * Hot-path geometry helpers. The scalar implementation is deliberately bit-stable;
 * it is the fallback used on runtimes where the Vector API is unavailable.
 */
public final class GeoMath {
    private GeoMath() { }

    public static float distance3d(int x1, int y1, int z1, int x2, int y2, int z2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float dz = (z1 - z2) / 16.0f;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static boolean withinSquaredDistance(int x1, int y1, int z1, int x2, int y2, int z2, float maxDistance) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float dz = (z1 - z2) / 16.0f;
        float max = maxDistance * maxDistance;
        return dx * dx + dy * dy + dz * dz <= max;
    }

    /**
     * Heurística Octile 3D admissível para A* em malha 8-conectada.
     *
     * Custos nativos: cardinal = 10, diagonal = 14 (subestima levemente 10*sqrt(2) ≈ 14.142 → admissível).
     * Z é tratado como componente linear de subida/descida acoplado.
     *
     * Fórmula: H = 10 * (max(dx, dy) + dz) + 4 * min(dx, dy)
     *
     * Zero alocação, zero Math.sqrt, ~3ns no hot path.
     * Pré-condição: dx, dy, dz devem ser não-negativos (passar Math.abs).
     */
    public static int heuristicOctile3D(int dx, int dy, int dz) {
        int maxXY = dx > dy ? dx : dy;
        int minXY = dx < dy ? dx : dy;
        return 10 * (maxXY + dz) + 4 * minXY;
    }
}
