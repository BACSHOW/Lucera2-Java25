package l2.gameserver.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkillFloodTrackerTest {
    private SkillFloodTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new SkillFloodTracker();
    }

    @Test
    @DisplayName("Primeiro pacote de skill deve sempre ser aceito")
    void firstPacketIsAlwaysAccepted() {
        assertTrue(tracker.tryMarkCastPacketSeen(1015, 12345, 250L, 1000L));
        assertEquals(1015, tracker.getLastPacketSkillId());
        assertEquals(12345, tracker.getLastPacketTargetObjId());
        assertEquals(1000L, tracker.getLastPacketAt());
    }

    @Test
    @DisplayName("Pacote com mesmo skillId e targetId dentro da janela de debounce deve ser descartado")
    void duplicatePacketWithinDebounceIsDropped() {
        assertTrue(tracker.tryMarkCastPacketSeen(1015, 12345, 250L, 1000L));
        // 100ms depois (dentro dos 250ms de debounce)
        assertFalse(tracker.tryMarkCastPacketSeen(1015, 12345, 250L, 1100L));
    }

    @Test
    @DisplayName("Pacote com mesmo skillId e targetId após expirar o debounce deve ser aceito")
    void packetAfterDebounceExpiresIsAccepted() {
        assertTrue(tracker.tryMarkCastPacketSeen(1015, 12345, 250L, 1000L));
        // 251ms depois (após janela de 250ms)
        assertTrue(tracker.tryMarkCastPacketSeen(1015, 12345, 250L, 1251L));
    }

    @Test
    @DisplayName("Mudança de target reseta o debounce imediatamente")
    void differentTargetBypassesDebounce() {
        assertTrue(tracker.tryMarkCastPacketSeen(1015, 12345, 250L, 1000L));
        // Mesmo skillId mas target diferente a apenas 50ms
        assertTrue(tracker.tryMarkCastPacketSeen(1015, 67890, 250L, 1050L));
    }

    @Test
    @DisplayName("Mudança de skill reseta o debounce imediatamente")
    void differentSkillBypassesDebounce() {
        assertTrue(tracker.tryMarkCastPacketSeen(1015, 12345, 250L, 1000L));
        // Mesmo target mas skill diferente a apenas 50ms
        assertTrue(tracker.tryMarkCastPacketSeen(1204, 12345, 250L, 1050L));
    }

    @Test
    @DisplayName("ActionFailed repetido para o mesmo skillId dentro do cooldown deve ser suprimido")
    void actionFailedIsSuppressedWithinCooldown() {
        tracker.markActionFailed(1015, 1000L);
        // Apenas 200ms depois, mesma skill -> suprime ActionFailed redundante
        assertTrue(tracker.shouldSuppressActionFailed(1015, 500L, 1200L));
    }

    @Test
    @DisplayName("ActionFailed para skill diferente não deve ser suprimido")
    void actionFailedForDifferentSkillIsNotSuppressed() {
        tracker.markActionFailed(1015, 1000L);
        // Skill diferente não é suprimida
        assertFalse(tracker.shouldSuppressActionFailed(1204, 500L, 1200L));
    }

    @Test
    @DisplayName("ActionFailed após expirar o cooldown pode ser enviado novamente")
    void actionFailedAfterCooldownExpiresIsNotSuppressed() {
        tracker.markActionFailed(1015, 1000L);
        // 501ms depois (após janela de 500ms)
        assertFalse(tracker.shouldSuppressActionFailed(1015, 500L, 1501L));
    }
}
