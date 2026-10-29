package l2.gameserver.stats;

import l2.gameserver.Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatsCapL2OffComplianceTest {

    @Test
    @DisplayName("L2OFF Caps: Blow Rate Cap Geral e Backstab respeitam tetos exatos")
    void testBlowRateCaps() {
        Config.BLOW_RATE_CAP_GENERAL = 800;
        Config.BLOW_RATE_CAP_BACKSTAB = 1000;

        // Blow geral com 950 -> deve sofrer clamp para 800 (80%)
        assertEquals(800, Formulas.clampBlowRate(950, false));
        // Blow geral com 650 -> mantem 650
        assertEquals(650, Formulas.clampBlowRate(650, false));

        // Backstab com 1200 -> clamp para 1000 (100%)
        assertEquals(1000, Formulas.clampBlowRate(1200, true));
        // Backstab com 900 -> mantem 900
        assertEquals(900, Formulas.clampBlowRate(900, true));
    }

    @Test
    @DisplayName("L2OFF Caps: Crítico físico não ultrapassa 500 (50.0%)")
    void testPhysicalCriticalRateCap() {
        Config.PHYSICAL_CRITICAL_RATE_CAP = 500;

        // Taxa de critico 750 -> deve limitar em 500
        assertEquals(500.0, Formulas.clampPhysicalCritRate(750.0), 0.001);
        // Taxa 420 -> mantem 420
        assertEquals(420.0, Formulas.clampPhysicalCritRate(420.0), 0.001);
    }

    @Test
    @DisplayName("L2OFF Caps: Crítico mágico não ultrapassa 32.0%")
    void testMagicCriticalRateCap() {
        Config.MAGIC_CRITICAL_RATE_CAP = 32.0;

        // Taxa de critico magico 45.0 -> deve limitar em 32.0
        assertEquals(32.0, Formulas.clampMagicCritRate(45.0), 0.001);
        // Taxa 25.0 -> mantem 25.0
        assertEquals(25.0, Formulas.clampMagicCritRate(25.0), 0.001);
    }
}
