package l2.gameserver.network.l2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/** Bounded outbound packet audit trail for diagnosing malformed or flooded sessions. */
public final class PacketAuditTrail {
    public record Entry(long timestampNanos, String packetType, int opcode, int payloadBytes, boolean serialized) {}
    private static final int CAPACITY = 256;
    private final Deque<Entry> entries = new ArrayDeque<>(CAPACITY);

    public synchronized void record(String packetType, int opcode, int payloadBytes, boolean serialized) {
        if (this.entries.size() == CAPACITY) this.entries.removeFirst();
        this.entries.addLast(new Entry(System.nanoTime(), packetType, opcode, payloadBytes, serialized));
    }

    public synchronized List<Entry> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(this.entries));
    }
}
