package l2.gameserver.geodata;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeoMathTest {
    @Test
    void computesScaledThreeDimensionalDistance() {
        assertThat(GeoMath.distance3d(0, 0, 0, 3, 4, 0)).isEqualTo(5.0f);
        assertThat(GeoMath.withinSquaredDistance(0, 0, 0, 3, 4, 0, 5.0f)).isTrue();
        assertThat(GeoMath.withinSquaredDistance(0, 0, 0, 3, 4, 0, 4.99f)).isFalse();
    }

    @Test
    void octile3DHeuristicIsAdmissibleAndZeroSqrt() {
        // H=0 para coordenadas idênticas
        assertThat(GeoMath.heuristicOctile3D(0, 0, 0)).isEqualTo(0);

        // 5 passos cardinais puros (5 * 10 = 50)
        assertThat(GeoMath.heuristicOctile3D(5, 0, 0)).isEqualTo(50);
        assertThat(GeoMath.heuristicOctile3D(0, 5, 0)).isEqualTo(50);

        // 5 passos diagonais puros (5 * 14 = 70)
        assertThat(GeoMath.heuristicOctile3D(5, 5, 0)).isEqualTo(70);

        // 3 diagonais + 2 cardinais (3 * 14 + 2 * 10 = 62)
        assertThat(GeoMath.heuristicOctile3D(5, 3, 0)).isEqualTo(62);

        // Com componente vertical Z (5 diag + 10 verticais = 5 * 14 + 10 * 10 = 170)
        assertThat(GeoMath.heuristicOctile3D(5, 5, 10)).isEqualTo(170);

        // Invariante de admissibilidade: para qualquer combinação (dx, dy), H nunca supera o custo ótimo real
        for (int dx = 0; dx < 20; dx++) {
            for (int dy = 0; dy < 20; dy++) {
                int h = GeoMath.heuristicOctile3D(dx, dy, 0);
                int real = 10 * Math.max(dx, dy) + 4 * Math.min(dx, dy);
                assertThat(h).as("dx=%d dy=%d", dx, dy).isEqualTo(real);
            }
        }
    }
}
