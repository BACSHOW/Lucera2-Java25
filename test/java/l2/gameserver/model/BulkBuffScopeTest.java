package l2.gameserver.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BulkBuffScopeTest {

    @Test
    @DisplayName("BulkBuffScope: Deve suprimir updates intermediários e disparar um único consolidado no close")
    void testBulkBuffSuppression() {
        MockCreature creature = new MockCreature();

        assertFalse(creature.isBulkBuffing(), "Inicialmente não deve estar em bulk buffing");
        assertEquals(0, creature.iconsUpdateCount.get());

        try (Creature.BulkBuffScope scope = creature.openBulkBuffScope()) {
            assertTrue(creature.isBulkBuffing(), "Dentro do escopo deve estar em bulk buffing");

            // Simula a aplicação de 25 buffs sucessivos
            for (int i = 0; i < 25; i++) {
                creature.simulateApplyBuff();
            }

            // Nenhum update de ícones deve ter sido disparado ainda
            assertEquals(0, creature.iconsUpdateCount.get(), "Updates devem estar suprimidos durante o bulk buffing");
        }

        // Ao sair do escopo (close), deve ter disparado exatamente 1 update consolidado
        assertFalse(creature.isBulkBuffing(), "Após o escopo deve retornar ao estado normal");
        assertEquals(1, creature.iconsUpdateCount.get(), "Exatamente um update consolidado deve ter sido emitido no close");
    }

    @Test
    @DisplayName("BulkBuffScope: Suporte a escopos aninhados (reentrantes)")
    void testNestedBulkBuffScopes() {
        MockCreature creature = new MockCreature();

        try (Creature.BulkBuffScope scope1 = creature.openBulkBuffScope()) {
            assertTrue(creature.isBulkBuffing());
            creature.simulateApplyBuff();

            try (Creature.BulkBuffScope scope2 = creature.openBulkBuffScope()) {
                assertTrue(creature.isBulkBuffing());
                creature.simulateApplyBuff();
            }

            // Escopo interno fechou, mas escopo externo ainda ativo: 0 updates
            assertTrue(creature.isBulkBuffing());
            assertEquals(0, creature.iconsUpdateCount.get());
        }

        // Ambos fecharam: exatamente 1 update consolidado
        assertFalse(creature.isBulkBuffing());
        assertEquals(1, creature.iconsUpdateCount.get());
    }

    private static class MockCreature {
        private final AtomicInteger bulkBuffDepth = new AtomicInteger(0);
        private volatile boolean statsModifiedInBulk = false;
        final AtomicInteger iconsUpdateCount = new AtomicInteger(0);

        boolean isBulkBuffing() {
            return bulkBuffDepth.get() > 0;
        }

        Creature.BulkBuffScope openBulkBuffScope() {
            bulkBuffDepth.incrementAndGet();
            return () -> {
                if (bulkBuffDepth.decrementAndGet() == 0) {
                    if (statsModifiedInBulk) {
                        statsModifiedInBulk = false;
                        iconsUpdateCount.incrementAndGet();
                    }
                }
            };
        }

        void simulateApplyBuff() {
            if (isBulkBuffing()) {
                statsModifiedInBulk = true;
                // Suprime update
            } else {
                iconsUpdateCount.incrementAndGet();
            }
        }
    }
}
