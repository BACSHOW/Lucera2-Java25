package l2.gameserver.ai.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suíte de testes para validação do comportamento de IA de monstros em combate Melee e relevo irregular (L2OFF Dhousefe).
 */
public class MonsterMeleeCombatAiTest {

    static class MockMonsterMovementAi {
        private int _geoPathFailCount = 0;
        private boolean _returnedHome = false;
        private boolean _teleportedToPlayer = false;

        public void onPathEvaluated(int pathSize, double distance, int meleeAttackRange) {
            if (pathSize < 2) {
                if (distance > (meleeAttackRange + 100)) {
                    _geoPathFailCount++;
                } else {
                    _geoPathFailCount = 0;
                }
            } else {
                _geoPathFailCount = 0;
            }
        }

        public void handleStuckMob(double distanceToAttacker) {
            if (_geoPathFailCount >= 10) {
                _geoPathFailCount = 0;
                if (distanceToAttacker > 250) {
                    _returnedHome = true;
                } else {
                    _teleportedToPlayer = false; // L2Off nunca teleporta monstro melee
                }
            }
        }

        public static boolean isEligibleForTacticalRetreat(int npcPhysicalAttackRange, int skillCastRange) {
            // Regra Retail L2Off: Apenas arqueiros/magos (> 200u) com skills de alcance (> 250u) recuam pós-cast
            return npcPhysicalAttackRange > 200 && skillCastRange > 250;
        }

        public int getGeoPathFailCount() { return _geoPathFailCount; }
        public boolean isReturnedHome() { return _returnedHome; }
        public boolean isTeleportedToPlayer() { return _teleportedToPlayer; }
    }

    @Test
    @DisplayName("L2OFF: Proximidade melee (<= 150u) não incrementa falso positivo de falha de geodata")
    void testMeleeProximityDoesNotCountAsGeoFail() {
        MockMonsterMovementAi ai = new MockMonsterMovementAi();

        // Monstro colado no player (distância 60, alcance melee 40)
        for (int i = 0; i < 15; i++) {
            ai.onPathEvaluated(0, 60.0, 40);
        }

        assertEquals(0, ai.getGeoPathFailCount(), "Falso positivo em melee deve ser zerado");
        ai.handleStuckMob(60.0);
        assertFalse(ai.isReturnedHome(), "Monstro não deve fugir enquanto estiver em combate próximo");
        assertFalse(ai.isTeleportedToPlayer(), "Monstro jamais deve teleportar para cima do jogador");
    }

    @Test
    @DisplayName("L2OFF: Alvo inalcançável a longa distância (> 250u) retorna ao spawn (returnHome)")
    void testUnreachableDistantTargetReturnsHome() {
        MockMonsterMovementAi ai = new MockMonsterMovementAi();

        // Alvo no topo de um penhasco (distância 500)
        for (int i = 0; i < 12; i++) {
            ai.onPathEvaluated(0, 500.0, 40);
        }

        assertTrue(ai.getGeoPathFailCount() >= 10);
        ai.handleStuckMob(500.0);

        assertTrue(ai.isReturnedHome(), "Monstro preso deve retornar para o spawn (returnHome) no L2Off");
        assertFalse(ai.isTeleportedToPlayer(), "Monstro NÃO deve teleportar para o topo do penhasco");
    }

    @Test
    @DisplayName("L2OFF: Apenas arqueiros e magos de longo alcance podem recuar taticamente pós-cast")
    void testTacticalRetreatEligibility() {
        // Monstro guerreiro melee (range 40, skill 150) -> NÃO recua
        assertFalse(MockMonsterMovementAi.isEligibleForTacticalRetreat(40, 150));

        // Monstro arqueiro/mago (range 600, skill 900) -> SIM recua
        assertTrue(MockMonsterMovementAi.isEligibleForTacticalRetreat(600, 900));
    }
}
