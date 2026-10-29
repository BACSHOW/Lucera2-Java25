package l2.gameserver.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpatialRadiusOptimizationTest {

    @Test
    @DisplayName("GameObject: isInStrictRadius deve calcular com exatidão quadrática e early-exit")
    void testStrictRadiusCalculation() {
        // Ponto A(0,0,0) -> Ponto B(30, 40, 0), distância Euclidiana = 50
        // Raio 49 -> fora
        assertFalse(GameObject.isInStrictRadius(0, 0, 0, 30, 40, 0, 0, 0, 49));
        // Raio 50 -> borda (estritamente menor < r^2 -> false)
        assertFalse(GameObject.isInStrictRadius(0, 0, 0, 30, 40, 0, 0, 0, 50));
        // Raio 51 -> dentro
        assertTrue(GameObject.isInStrictRadius(0, 0, 0, 30, 40, 0, 0, 0, 51));
    }

    @Test
    @DisplayName("GameObject: isInStrictRadius com diferença em Z além do raio deve abortar imediatamente")
    void testStrictRadiusEarlyExitZ() {
        // Delta Z = 200, raio = 100 -> deve abortar antes de multiplicar
        assertFalse(GameObject.isInStrictRadius(0, 0, 0, 10, 10, 200, 0, 0, 100));
    }

    @Test
    @DisplayName("GameObject: isInStrictRadius considerando raio de colisão de criaturas")
    void testStrictRadiusWithCollision() {
        // Ponto A col=10, Ponto B col=10, distância = 60, raio = 45
        // TotalRadius = 45 + 10 + 10 = 65 -> 60 < 65 -> true
        assertTrue(GameObject.isInStrictRadius(0, 0, 0, 60, 0, 0, 10, 10, 45));
        // Raio 35 -> TotalRadius = 35 + 20 = 55 -> 60 < 55 -> false
        assertFalse(GameObject.isInStrictRadius(0, 0, 0, 60, 0, 0, 10, 10, 35));
    }
}
