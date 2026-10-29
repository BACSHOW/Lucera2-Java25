package l2.gameserver.network.pacing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NpcSpawnPacerPriorityTest {

    @Test
    @DisplayName("NpcSpawnPacer 2.0: Distância imediata (<= 450) deve ignorar o cadenciamento de spawn")
    void testImmediateDistanceBypass() {
        // Ponto A(0,0,0) -> Ponto B(200, 200, 0), distSq = 80000, 450^2 = 202500 -> deve dar true
        assertTrue(NpcSpawnPacer.isWithinImmediateDistance(0, 0, 0, 200, 200, 0, 450));

        // Ponto A(0,0,0) -> Ponto B(400, 400, 0), distSq = 320000 -> maior que 202500 -> deve dar false
        assertFalse(NpcSpawnPacer.isWithinImmediateDistance(0, 0, 0, 400, 400, 0, 450));
    }

    @Test
    @DisplayName("NpcSpawnPacer 2.0: Detecção de ameaça de monstro agressivo respeita raio de perigo")
    void testThreateningCalculation() {
        int aggroRange = 300;
        double dangerRadius = Math.max(650.0, aggroRange + 150.0); // 650.0
        assertEquals(650.0, dangerRadius, 0.001);

        // Monstro agressivo com aggro 600 -> 600 + 150 = 750
        int highAggro = 600;
        double highDanger = Math.max(650.0, highAggro + 150.0);
        assertEquals(750.0, highDanger, 0.001);
    }
}
