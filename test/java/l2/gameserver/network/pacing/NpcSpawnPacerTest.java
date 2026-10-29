package l2.gameserver.network.pacing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class NpcSpawnPacerTest {

    /**
     * Harness que reproduz a state machine do NpcSpawnPacer para validação determinística sem dependências pesadas de rede.
     */
    static class TestPacerStateMachine {
        final IntArrayList queue = new IntArrayList(32);
        final IntOpenHashSet set = new IntOpenHashSet(32);
        int recentBurstCount = 0;
        long lastBurstResetTime = 0L;
        int burstLimit;
        int batchSize;
        long burstResetMs;

        final List<Integer> dispatched = new ArrayList<>();
        final AtomicInteger dispatchRuns = new AtomicInteger();

        TestPacerStateMachine(int burstLimit, int batchSize, long burstResetMs) {
            this.burstLimit = burstLimit;
            this.batchSize = batchSize;
            this.burstResetMs = burstResetMs;
        }

        enum Outcome { BURST_IMMEDIATE, QUEUED, ALREADY_QUEUED }

        Outcome enqueue(int objId, long now) {
            if (now - lastBurstResetTime > burstResetMs) {
                recentBurstCount = 0;
                lastBurstResetTime = now;
            }
            if (queue.isEmpty() && recentBurstCount < burstLimit) {
                recentBurstCount++;
                dispatched.add(objId);
                return Outcome.BURST_IMMEDIATE;
            }
            if (set.add(objId)) {
                queue.add(objId);
                return Outcome.QUEUED;
            }
            return Outcome.ALREADY_QUEUED;
        }

        boolean cancel(int objId) {
            if (set.remove(objId)) {
                queue.rem(objId);
                return true;
            }
            return false;
        }

        int dispatchOnce() {
            dispatchRuns.incrementAndGet();
            int n = 0;
            while (!queue.isEmpty() && n < batchSize) {
                int id = queue.removeInt(0);
                set.remove(id);
                dispatched.add(id);
                n++;
            }
            return n;
        }
    }

    @Test
    @DisplayName("Burst imediato despacha até o limite sem enfileirar")
    void immediateBurstDispatchesUpToLimitWithoutQueueing() {
        TestPacerStateMachine p = new TestPacerStateMachine(3, 2, 150);
        assertEquals(TestPacerStateMachine.Outcome.BURST_IMMEDIATE, p.enqueue(1, 1000));
        assertEquals(TestPacerStateMachine.Outcome.BURST_IMMEDIATE, p.enqueue(2, 1050));
        assertEquals(TestPacerStateMachine.Outcome.BURST_IMMEDIATE, p.enqueue(3, 1100));
        // O 4º excede o burst -> vai para a fila cadenciada
        assertEquals(TestPacerStateMachine.Outcome.QUEUED, p.enqueue(4, 1150));
        assertEquals(1, p.queue.size());
        assertEquals(4, p.queue.getInt(0));
        assertTrue(p.set.contains(4));
    }

    @Test
    @DisplayName("Janela de burst reseta após o tempo configurado")
    void burstResetsAfterResetWindow() {
        TestPacerStateMachine p = new TestPacerStateMachine(2, 2, 150);
        p.enqueue(1, 1000);
        p.enqueue(2, 1050);
        // 160ms depois -> janela resetou
        assertEquals(TestPacerStateMachine.Outcome.BURST_IMMEDIATE, p.enqueue(3, 1210));
        assertEquals(1, p.recentBurstCount);
    }

    @Test
    @DisplayName("Enfileiramento duplicado com mesmo ID é idempotente")
    void duplicateEnqueueIsIdempotent() {
        TestPacerStateMachine p = new TestPacerStateMachine(1, 4, 150);
        p.enqueue(1, 1000);              // burst
        assertEquals(TestPacerStateMachine.Outcome.QUEUED, p.enqueue(1, 1050)); // queued
        assertEquals(TestPacerStateMachine.Outcome.ALREADY_QUEUED, p.enqueue(1, 1100)); // já na fila -> no-op
        assertEquals(1, p.queue.size());
        assertEquals(1, p.set.size());
    }

    @Test
    @DisplayName("Drenagem de lotes honra o batchSize e esvazia a fila")
    void dispatchBatchHonorsBatchSizeAndDrainsQueue() {
        TestPacerStateMachine p = new TestPacerStateMachine(0, 3, 150);
        for (int i = 0; i < 7; i++) {
            p.enqueue(100 + i, 1000 + i);
        }
        assertEquals(7, p.queue.size());

        int sent = p.dispatchOnce();
        assertEquals(3, sent);
        assertEquals(4, p.queue.size());

        sent = p.dispatchOnce();
        assertEquals(3, sent);
        assertEquals(1, p.queue.size());

        sent = p.dispatchOnce();
        assertEquals(1, sent);
        assertTrue(p.queue.isEmpty());
        assertTrue(p.set.isEmpty());
    }

    @Test
    @DisplayName("Coalescing: cancelPending remove NPC pendente da fila e retorna true")
    void cancelPendingRemovesAndReturnsTrueWhenPending() {
        TestPacerStateMachine p = new TestPacerStateMachine(0, 4, 150);
        p.enqueue(42, 1000);
        assertTrue(p.cancel(42));
        assertTrue(p.queue.isEmpty());
        assertTrue(p.set.isEmpty());
    }

    @Test
    @DisplayName("Coalescing: cancelPending retorna false quando NPC já foi despachado para a rede")
    void cancelPendingReturnsFalseWhenAlreadyDispatched() {
        TestPacerStateMachine p = new TestPacerStateMachine(2, 4, 150);
        p.enqueue(7, 1000);    // burst imediato
        assertFalse(p.cancel(7));   // já saiu, DeleteObject deve ser enviado normalmente
        // Novo enqueue funciona normalmente
        assertEquals(TestPacerStateMachine.Outcome.BURST_IMMEDIATE, p.enqueue(7, 1160));
    }

    @Test
    @DisplayName("dispatchOnce em fila vazia é no-op")
    void dispatchOnceOnEmptyQueueIsNoOp() {
        TestPacerStateMachine p = new TestPacerStateMachine(2, 4, 150);
        assertEquals(0, p.dispatchOnce());
        assertEquals(1, p.dispatchRuns.get());
    }

    @Test
    @DisplayName("Após esgotar o burst, novos NPCs vão para a fila mesmo em intervalos curtos")
    void enqueueAfterBurstExhaustionGoesToQueueEvenWithDelay() {
        TestPacerStateMachine p = new TestPacerStateMachine(1, 2, 150);
        p.enqueue(1, 1000);
        assertEquals(TestPacerStateMachine.Outcome.QUEUED, p.enqueue(2, 1050));
    }
}
