package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestBypassFloodMitigationTest {

    static class MockBypassDebouncer {
        private String _lastBypassCommand = "";
        private long _lastBypassTime = 0L;
        private int _bypassCountInSecond = 0;
        private long _bypassSecondStartTick = 0L;

        public synchronized boolean checkAndSetBypassDebounce(String cmd, long windowMs, int maxPerSec) {
            final long now = System.currentTimeMillis();

            // 1. Teto anti-DOS por segundo
            if (maxPerSec > 0) {
                if (now - _bypassSecondStartTick >= 1000L) {
                    _bypassSecondStartTick = now;
                    _bypassCountInSecond = 1;
                } else {
                    _bypassCountInSecond++;
                    if (_bypassCountInSecond > maxPerSec) {
                        return false;
                    }
                }
            }

            // 2. Deduplicação do mesmo comando dentro da janela windowMs
            if (windowMs > 0 && cmd != null && cmd.equals(_lastBypassCommand)) {
                if (now - _lastBypassTime < windowMs) {
                    return false;
                }
            }

            _lastBypassCommand = (cmd != null) ? cmd : "";
            _lastBypassTime = now;
            return true;
        }
    }

    @Test
    @DisplayName("Bypass Flood: Cliques repetidos no mesmo comando em < 250ms devem ser descartados (debounce)")
    void testDebounceIdenticalCommands() {
        MockBypassDebouncer debouncer = new MockBypassDebouncer();

        // Primeiro clique passa
        assertTrue(debouncer.checkAndSetBypassDebounce("buy_potion", 250, 15));

        // Segundo clique imediato no mesmo botão (double-click acidental ou macro) -> drop silencioso
        assertFalse(debouncer.checkAndSetBypassDebounce("buy_potion", 250, 15));

        // Outro comando diferente -> passa imediatamente
        assertTrue(debouncer.checkAndSetBypassDebounce("open_community_board", 250, 15));
    }

    @Test
    @DisplayName("Bypass Flood: Teto global de 15 comandos por segundo (anti-DoS)")
    void testRateLimitMaxPerSecond() {
        MockBypassDebouncer debouncer = new MockBypassDebouncer();

        // 15 comandos variados passam
        for (int i = 0; i < 15; i++) {
            assertTrue(debouncer.checkAndSetBypassDebounce("cmd_" + i, 250, 15), "Comando #" + i + " deve passar");
        }

        // O 16º comando no mesmo segundo é barrado pelo teto anti-DoS
        assertFalse(debouncer.checkAndSetBypassDebounce("cmd_16", 250, 15), "16º comando deve sofrer drop por rate-limit");
    }
}
