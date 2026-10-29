package l2.gameserver.geodata.pathfinding;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Estrutura binária compacta de alta performance (Zero-GC / Cap'n Proto Wire Format)
 * para representação de trajetórias e troca de waypoints entre o motor de busca e as threads de movimento.
 *
 * Layout do Buffer (Little-Endian):
 * [0x00] magic (4B): 0x50415448 ('PATH')
 * [0x04] version (2B): 1
 * [0x06] waypointCount (2B): N
 * [0x08] originX (4B), originY (4B), originZ (4B)
 * [0x14] targetX (4B), targetY (4B), targetZ (4B)
 * [0x20] totalDistance (4B Float)
 * [0x24] N x Waypoints: [x: 4B, y: 4B, z: 4B] (12B por ponto)
 */
public final class PathTrajectoryBinary {
    public static final int MAGIC = 0x50415448; // 'PATH'
    public static final short VERSION = 1;
    public static final int HEADER_SIZE = 36;
    public static final int WAYPOINT_SIZE = 12;

    private PathTrajectoryBinary() {
        // Utilitário
    }

    public static int calculateBufferSize(int waypointCount) {
        return HEADER_SIZE + (waypointCount * WAYPOINT_SIZE);
    }

    public static void serialize(
        ByteBuffer dst,
        int ox, int oy, int oz,
        int tx, int ty, int tz,
        float totalDistance,
        int[] waypointsX,
        int[] waypointsY,
        int[] waypointsZ,
        int count
    ) {
        dst.order(ByteOrder.LITTLE_ENDIAN);
        dst.putInt(MAGIC);
        dst.putShort(VERSION);
        dst.putShort((short) count);
        dst.putInt(ox);
        dst.putInt(oy);
        dst.putInt(oz);
        dst.putInt(tx);
        dst.putInt(ty);
        dst.putInt(tz);
        dst.putFloat(totalDistance);

        for (int i = 0; i < count; i++) {
            dst.putInt(waypointsX[i]);
            dst.putInt(waypointsY[i]);
            dst.putInt(waypointsZ[i]);
        }
    }

    public static boolean isValid(ByteBuffer src) {
        if (src.remaining() < HEADER_SIZE) {
            return false;
        }
        int position = src.position();
        src.order(ByteOrder.LITTLE_ENDIAN);
        int magic = src.getInt(position);
        short version = src.getShort(position + 4);
        return magic == MAGIC && version == VERSION;
    }

    public static int getWaypointCount(ByteBuffer src) {
        return src.getShort(src.position() + 6) & 0xFFFF;
    }

    public static float getTotalDistance(ByteBuffer src) {
        return src.getFloat(src.position() + 32);
    }

    public static void readWaypoint(ByteBuffer src, int index, int[] outXyz) {
        int offset = src.position() + HEADER_SIZE + (index * WAYPOINT_SIZE);
        outXyz[0] = src.getInt(offset);
        outXyz[1] = src.getInt(offset + 4);
        outXyz[2] = src.getInt(offset + 8);
    }
}
