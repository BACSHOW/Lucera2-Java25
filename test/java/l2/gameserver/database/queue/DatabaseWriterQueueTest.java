package l2.gameserver.database.queue;

import l2.gameserver.model.state.PlayerDirtyFlags;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseWriterQueueTest {

    @Test
    public void testPlayerDirtyFlagsBitmask() {
        int flags = PlayerDirtyFlags.DIRTY_NONE;
        assertEquals(0, flags);

        // Ativar coordenadas e vitais
        flags |= PlayerDirtyFlags.DIRTY_COORDINATES;
        flags |= PlayerDirtyFlags.DIRTY_VITALS;

        assertTrue((flags & PlayerDirtyFlags.DIRTY_COORDINATES) != 0);
        assertTrue((flags & PlayerDirtyFlags.DIRTY_VITALS) != 0);
        assertFalse((flags & PlayerDirtyFlags.DIRTY_PVP_PK) != 0);

        // Resetar coordenadas
        flags &= ~PlayerDirtyFlags.DIRTY_COORDINATES;
        assertFalse((flags & PlayerDirtyFlags.DIRTY_COORDINATES) != 0);
        assertTrue((flags & PlayerDirtyFlags.DIRTY_VITALS) != 0);
    }

    @Test
    public void testDbWriteTaskPartitioning() {
        AtomicInteger executedCount = new AtomicInteger(0);
        DbWriteTask task = new DbWriteTask() {
            @Override
            public void execute(java.sql.Connection conn) {
                executedCount.incrementAndGet();
            }

            @Override
            public int getPartitionKey() {
                return 268484123; // ObjectId de player
            }
        };

        assertEquals(268484123, task.getPartitionKey());
    }
}
