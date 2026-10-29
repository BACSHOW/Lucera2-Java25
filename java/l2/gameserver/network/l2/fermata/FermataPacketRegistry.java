/*
 * L2JLucera2 — Fermata C2S packet registry / dispatcher.
 *
 * Maps subOpcode values to factory lambdas that instantiate the right
 * FermataClientPacket. Unknown subOpcodes fall back to FermataDummyPacket so
 * a misbehaving client cannot escalate to a disconnect on stray bytes.
 */
package l2.gameserver.network.l2.fermata;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.FermataDummyPacket;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.fermata.c2s.RequestFermataClientCapabilities;

/**
 * Static, thread-safe registry of Fermata inbound packets.
 *
 * <p>The registry holds {@link Supplier} factories (not instances) so each
 * connection gets its own packet object.
 */
public final class FermataPacketRegistry {

    /** SubOpcode for the capability handshake. */
    public static final int SUB_OPCODE_REQUEST_CAPABILITIES = 0x0000;
    public static final int SUB_OPCODE_REMOVE_BUFF = 0x0001;
    public static final int SUB_OPCODE_ITEM_LINK_CHAT = 0x0002;
    public static final int SUB_OPCODE_NPC_DROPS = 0x0004;
    public static final int SUB_OPCODE_LINKED_CHAT = 0x000A;
    public static final int SUB_OPCODE_ATLAS_CATALOG = 0x000B;
    public static final int SUB_OPCODE_ITEM_SOURCES = 0x000C;
    public static final int SUB_OPCODE_NPC_ATLAS_ENTRY = 0x000D;
    public static final int SUB_OPCODE_EQUIPMENT_PREVIEW = 0x000E;
    public static final int SUB_OPCODE_COORDINATION = 0x000F;
    public static final int SUB_OPCODE_PARTY_PING = 0x0010;
    public static final int SUB_OPCODE_ECONOMY_V1 = 0x0100;
    public static final int SUB_OPCODE_COMBAT_RECORD = 0x0101;
    public static final int SUB_OPCODE_REPORT_TRANSFER = 0x0102;
    public static final int SUB_OPCODE_FIGHT_LINK_CHAT = 0x0103;

    /** Minimum subOpcode we accept from the client (inclusive). */
    public static final int MIN_SUB_OPCODE = 0x0000;

    /** Maximum subOpcode we accept from the client (inclusive). */
    public static final int MAX_SUB_OPCODE = 0x7FFF;

    /** Fallback packet used for unknown subOpcodes and truncated buffers. */
    public static final L2GameClientPacket UNKNOWN_PACKET = new FermataDummyPacket();

    private static final Map<Integer, Supplier<L2GameClientPacket>> FACTORIES = new HashMap<>(32);

    static {
        register(SUB_OPCODE_REQUEST_CAPABILITIES, RequestFermataClientCapabilities::new);
        register(SUB_OPCODE_REMOVE_BUFF, l2.gameserver.network.l2.fermata.c2s.RequestFermataRemoveBuff::new);
        register(SUB_OPCODE_ITEM_LINK_CHAT, l2.gameserver.network.l2.fermata.c2s.RequestFermataItemLinkChat::new);
        register(SUB_OPCODE_NPC_DROPS, l2.gameserver.network.l2.fermata.c2s.RequestFermataNpcDrops::new);
        register(SUB_OPCODE_LINKED_CHAT, l2.gameserver.network.l2.fermata.c2s.RequestFermataLinkedChat::new);
        register(SUB_OPCODE_ATLAS_CATALOG, l2.gameserver.network.l2.fermata.c2s.RequestFermataAtlasCatalog::new);
        register(SUB_OPCODE_ITEM_SOURCES, l2.gameserver.network.l2.fermata.c2s.RequestFermataItemSources::new);
        register(SUB_OPCODE_NPC_ATLAS_ENTRY, l2.gameserver.network.l2.fermata.c2s.RequestFermataNpcAtlasEntry::new);
        register(SUB_OPCODE_EQUIPMENT_PREVIEW, l2.gameserver.network.l2.fermata.c2s.RequestFermataEquipmentPreview::new);
        register(SUB_OPCODE_COORDINATION, l2.gameserver.network.l2.fermata.c2s.RequestFermataCoordination::new);
        register(SUB_OPCODE_PARTY_PING, l2.gameserver.network.l2.fermata.c2s.RequestFermataPartyPing::new);
        register(SUB_OPCODE_ECONOMY_V1, l2.gameserver.network.l2.fermata.c2s.RequestFermataEconomyV1::new);
        register(SUB_OPCODE_COMBAT_RECORD, l2.gameserver.network.l2.fermata.c2s.RequestFermataCombatRecord::new);
        register(SUB_OPCODE_REPORT_TRANSFER, l2.gameserver.network.l2.fermata.c2s.RequestFermataReportTransfer::new);
        register(SUB_OPCODE_FIGHT_LINK_CHAT, l2.gameserver.network.l2.fermata.c2s.RequestFermataFightLinkChat::new);
    }

    private FermataPacketRegistry() {
        // utility holder
    }

    /**
     * Registers a factory for the given subOpcode. Replaces any existing
     * mapping (caller is responsible for ordering when the same subOpcode is
     * claimed twice).
     *
     * @param subOpcode wire-stable subOpcode (LE short)
     * @param factory   supplier that returns a fresh instance on each call
     */
    public static synchronized void register(int subOpcode, Supplier<L2GameClientPacket> factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory must not be null");
        }
        FACTORIES.put(subOpcode & 0xFFFF, factory);
    }

    /** Removes the factory for {@code subOpcode}, if any. */
    public static synchronized void unregister(int subOpcode) {
        FACTORIES.remove(subOpcode & 0xFFFF);
    }

    /** Returns the registered factory count. Diagnostic helper. */
    public static int registeredCount() {
        return FACTORIES.size();
    }

    /**
     * Resolves the inbound packet for the given subOpcode and attaches the
     * current client's {@link FermataContext}. The supplied buffer has already
     * been advanced past the 0xFD opcode and the two-byte subOpcode.
     *
     * @param subOpcode wire subOpcode (LE short, 0..65535)
     * @param buffer    payload buffer (never {@code null})
     * @param client    owning client (never {@code null})
     * @return a freshly-allocated packet, or {@link #UNKNOWN_PACKET} when the
     *         subOpcode is unregistered
     */
    public static L2GameClientPacket resolve(int subOpcode, ByteBuffer buffer, GameClient client) {
        int masked = subOpcode & 0xFFFF;
        Supplier<L2GameClientPacket> factory = FACTORIES.get(masked);
        L2GameClientPacket packet;
        if (factory != null) {
            packet = factory.get();
        } else {
            packet = new FermataDummyPacket();
        }
        if (packet instanceof FermataClientPacket && client != null) {
            ((FermataClientPacket) packet).attachContext(client.getFermata());
        }
        return packet;
    }

    /**
     * Convenience: returns a safe fallback for truncated buffers.
     */
    public static L2GameClientPacket unknown() {
        return UNKNOWN_PACKET;
    }
}