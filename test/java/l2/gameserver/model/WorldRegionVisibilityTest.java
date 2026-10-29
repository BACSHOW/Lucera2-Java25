package l2.gameserver.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldRegionVisibilityTest {
    @Test
    void acceptsTargetsInsideOrOnRadius() {
        assertTrue(WorldRegion.isWithinVisibleRadius(0, 0, 3, 4, 5));
        assertTrue(WorldRegion.isWithinVisibleRadius(100, -100, 100, -100, 0));
    }

    @Test
    void rejectsTargetsOutsideRadius() {
        assertFalse(WorldRegion.isWithinVisibleRadius(0, 0, 4, 4, 5));
    }

    @Test
    void usesLongArithmeticForWorldCoordinates() {
        assertFalse(WorldRegion.isWithinVisibleRadius(Integer.MIN_VALUE, Integer.MIN_VALUE,
                Integer.MAX_VALUE, Integer.MAX_VALUE, 3500));
    }
}
