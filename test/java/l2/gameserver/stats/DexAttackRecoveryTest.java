package l2.gameserver.stats;

import l2.gameserver.Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DexAttackRecoveryTest {

    @Test
    @DisplayName("Formulas: calcDexAttackRecoveryFactor calcula redução linear baseada em DEX")
    void testDexScalingCalculation() {
        Config.ENABLE_DEX_ATTACK_RECOVERY_SCALING = true;
        Config.DEX_ATTACK_RECOVERY_BASE_DEX = 20;
        Config.DEX_ATTACK_RECOVERY_DEX_DIVISOR = 45.0;
        Config.DEX_ATTACK_RECOVERY_MAX_BONUS = 0.45;
        Config.DEX_ATTACK_RECOVERY_MIN_FACTOR = 0.50;
        Config.DEX_ATTACK_RECOVERY_MAX_FACTOR = 1.15;

        // DEX 20 (base) -> fator 1.0 (sem bônus/penalidade)
        double factorBase = Formulas.calculateDexFactor(20);
        assertEquals(1.0, factorBase, 0.001);

        // DEX 45 -> (45 - 20) / 45 * 0.45 = 25/45 * 0.45 = 0.25 -> 1.0 - 0.25 = 0.75 (25% mais rápido)
        double factor45 = Formulas.calculateDexFactor(45);
        assertEquals(0.75, factor45, 0.001);

        // DEX 65 (muito alta) -> (45/45) * 0.45 = 0.45 -> 1.0 - 0.45 = 0.55
        double factor65 = Formulas.calculateDexFactor(65);
        assertEquals(0.55, factor65, 0.001);

        // DEX 100 (extrema) -> clamped no min factor = 0.50
        double factor100 = Formulas.calculateDexFactor(100);
        assertEquals(0.50, factor100, 0.001);

        // DEX 10 (muito baixa) -> (10 - 20) / 45 * 0.45 = -0.10 -> 1.0 - (-0.10) = 1.10
        double factor10 = Formulas.calculateDexFactor(10);
        assertEquals(1.10, factor10, 0.001);
    }
}
