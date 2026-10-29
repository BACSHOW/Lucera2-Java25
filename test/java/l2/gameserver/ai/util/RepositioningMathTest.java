package l2.gameserver.ai.util;

import l2.gameserver.utils.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RepositioningMathTest {

    @Test
    @DisplayName("Direção cardinal pura Norte: NPC acima do alvo deve ter ux=0, uy=1")
    void cardinalNorth() {
        RepositioningMath.OrthogonalVectors v = RepositioningMath.computeOrthogonalVectors(1000, 1100, 1000, 1000, 0);
        assertFalse(v.degenerate);
        assertEquals(0.0, v.ux, 1e-9);
        assertEquals(1.0, v.uy, 1e-9);
        // left: rotação +90° -> (-uy, ux) = (-1, 0)
        assertEquals(-1.0, v.leftX, 1e-9);
        assertEquals(0.0, v.leftY, 1e-9);
        // right: rotação -90° -> (uy, -ux) = (1, 0)
        assertEquals(1.0, v.rightX, 1e-9);
        assertEquals(0.0, v.rightY, 1e-9);
        assertEquals(100.0, v.rawDistance, 1e-9);
    }

    @Test
    @DisplayName("Direção cardinal pura Leste: NPC à direita do alvo deve ter ux=1, uy=0")
    void cardinalEast() {
        RepositioningMath.OrthogonalVectors v = RepositioningMath.computeOrthogonalVectors(1100, 1000, 1000, 1000, 0);
        assertFalse(v.degenerate);
        assertEquals(1.0, v.ux, 1e-9);
        assertEquals(0.0, v.uy, 1e-9);
        assertEquals(0.0, v.leftX, 1e-9);
        assertEquals(1.0, v.leftY, 1e-9);
        assertEquals(0.0, v.rightX, 1e-9);
        assertEquals(-1.0, v.rightY, 1e-9);
    }

    @Test
    @DisplayName("Caso degenerate: NPC e alvo na mesma coordenada usam heading sem gerar NaN")
    void degenerateHeadingZero() {
        RepositioningMath.OrthogonalVectors v = RepositioningMath.computeOrthogonalVectors(1000, 1000, 1000, 1000, 0);
        assertTrue(v.degenerate);
        assertEquals(1.0, v.ux, 1e-9);
        assertEquals(0.0, v.uy, 1e-9);
        assertFalse(Double.isNaN(v.leftX));
        assertFalse(Double.isNaN(v.rightX));
    }

    @ParameterizedTest
    @MethodSource("directionsProvider")
    @DisplayName("Vetores mantêm norma unitária e produto escalar zero (ortogonalidade)")
    void orthogonalVectorsAreUnitAndOrthogonal(int dx, int dy) {
        RepositioningMath.OrthogonalVectors v = RepositioningMath.computeOrthogonalVectors(1000 + dx, 1000 + dy, 1000, 1000, 0);
        // Norma unitária
        assertEquals(1.0, v.ux * v.ux + v.uy * v.uy, 1e-9);
        assertEquals(1.0, v.leftX * v.leftX + v.leftY * v.leftY, 1e-9);
        assertEquals(1.0, v.rightX * v.rightX + v.rightY * v.rightY, 1e-9);
        // Ortogonalidade: dot product deve ser 0
        assertEquals(0.0, v.ux * v.leftX + v.uy * v.leftY, 1e-9);
        assertEquals(0.0, v.ux * v.rightX + v.uy * v.rightY, 1e-9);
    }

    static Stream<Arguments> directionsProvider() {
        return Stream.of(
                Arguments.of(0, 100),
                Arguments.of(0, -100),
                Arguments.of(100, 0),
                Arguments.of(-100, 0),
                Arguments.of(100, 100),
                Arguments.of(-100, 100),
                Arguments.of(100, -100),
                Arguments.of(-100, -100),
                Arguments.of(1, 1000),
                Arguments.of(1000, 1)
        );
    }

    @Test
    @DisplayName("isCollinear detecta linhas retas horizontais, verticais e diagonais")
    void collinearDetection() {
        assertTrue(RepositioningMath.isCollinear(new Location(0, 0, 0), new Location(5, 0, 0), new Location(10, 0, 0)));
        assertTrue(RepositioningMath.isCollinear(new Location(0, 0, 0), new Location(0, 5, 0), new Location(0, 10, 0)));
        assertTrue(RepositioningMath.isCollinear(new Location(0, 0, 0), new Location(5, 5, 0), new Location(10, 10, 0)));
        // Não-colinear
        assertFalse(RepositioningMath.isCollinear(new Location(0, 0, 0), new Location(5, 1, 0), new Location(10, 0, 0)));
    }

    @Test
    @DisplayName("suppressCollinear colapsa nós intermediários colineares mantendo início e fim")
    void suppressCollinearTest() {
        List<Location> path = List.of(
                new Location(0, 0, 0),
                new Location(2, 0, 0),
                new Location(5, 0, 0),
                new Location(7, 0, 0),
                new Location(10, 0, 0)
        );
        List<Location> simplified = RepositioningMath.suppressCollinear(path);
        assertEquals(2, simplified.size());
        assertEquals(new Location(0, 0, 0), simplified.get(0));
        assertEquals(new Location(10, 0, 0), simplified.get(1));
    }

    @Test
    @DisplayName("stringPull colapsa caminho reto quando há linha direta de visão transitável")
    void stringPullTest() {
        List<Location> path = List.of(
                new Location(0, 0, 0),
                new Location(2, 2, 0),
                new Location(4, 4, 0),
                new Location(6, 6, 0),
                new Location(8, 8, 0)
        );
        // Linha de visão sempre livre
        List<Location> pulled = RepositioningMath.stringPull(path, (from, to) -> true);
        assertEquals(2, pulled.size());
        assertEquals(new Location(0, 0, 0), pulled.get(0));
        assertEquals(new Location(8, 8, 0), pulled.get(1));
    }

    @Test
    @DisplayName("Boids Separation: Sobreposição exata dispersa radialmente sem gerar NaN ou zero force")
    void boidsExactOverlapScattersRadially() {
        // Dois NPCs exatamente na mesma coordenada (distSq = 0.0)
        double[] force = RepositioningMath.computeBoidsSeparationForce(1001, 500, 500, 15.0, 500, 500, 15.0, 10.0, 50.0);
        assertFalse(Double.isNaN(force[0]));
        assertFalse(Double.isNaN(force[1]));
        double magnitude = Math.hypot(force[0], force[1]);
        assertEquals(10.0, magnitude, 1e-6, "Magnitude deve ser igual à força máxima na dispersão radial");
    }

    @Test
    @DisplayName("Boids Separation: Proximidade física gera força de repulsão oposta")
    void boidsProximityRepulsion() {
        // Actor em (510, 500), vizinho em (500, 500) -> dx = 10, força deve empurrar para +X
        double[] force = RepositioningMath.computeBoidsSeparationForce(1002, 510, 500, 15.0, 500, 500, 15.0, 10.0, 50.0);
        assertTrue(force[0] > 0.0, "Força de repulsão deve empurrar para longe do vizinho em X");
        assertEquals(0.0, force[1], 1e-9, "Sem força em Y pois estão alinhados");
    }
}
