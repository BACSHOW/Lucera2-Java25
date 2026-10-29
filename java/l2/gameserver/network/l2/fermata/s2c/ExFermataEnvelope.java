/*
 * L2JLucera2 — Base class for Fermata S2C envelopes (opcode 0xFD + subOpcode).
 *
 * Writes the 0xFD opcode followed by a little-endian short subOpcode, then
 * delegates payload serialization to the subclass. Carries a required
 * capability bit that {@link FermataBroadcaster} checks before sending.
 */
package l2.gameserver.network.l2.fermata.s2c;

import l2.gameserver.network.l2.fermata.FermataCapabilities;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * Base envelope for all Fermata S2C packets. Each subclass:
 * <ul>
 *   <li>Defines its own {@code SUB_OPCODE_*} constant (wire-stable short).</li>
 *   <li>Overrides {@link #writeImpl()} and writes payload bytes AFTER
 *       the super-class has emitted 0xFD + subOpcode.</li>
 *   <li>Optionally overrides {@link #requiredCapability()} to gate broadcast.</li>
 * </ul>
 */
public abstract class ExFermataEnvelope extends L2GameServerPacket {

    /** Outer opcode used for every Fermata envelope. */
    public static final int FERMATA_OPCODE = 0xFD;

    private final int _subOpcode;

    protected ExFermataEnvelope(int subOpcode) {
        this._subOpcode = subOpcode & 0xFFFF;
    }

    /** Wire subOpcode exposed to subclasses (LE short, 0..65535). */
    protected final int subOpcode() {
        return this._subOpcode;
    }

    /**
     * Required capability bit. Defaults to 0 (no capability required).
     * Override to gate the broadcast on a specific negotiated bit.
     */
    public int requiredCapability() {
        return 0;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(FERMATA_OPCODE);
        this.writeH(this._subOpcode);
        this.writePayload();
    }

    /** Subclasses serialize payload after the 0xFD + subOpcode header. */
    protected abstract void writePayload();

    // --- Sub-opcode registry (kept here so all built-ins share one file) ---

    public static final class SubOpcodes {
        /** SetWeather - weather state broadcast. */
        public static final int SET_WEATHER = 0x0001;
        /** RestoreClientWeather - reset client weather. */
        public static final int RESTORE_CLIENT_WEATHER = 0x0002;
        /** ServerCapabilities - capability handshake response. */
        public static final int SERVER_CAPABILITIES = 0x0013;

        private SubOpcodes() {
            // constants only
        }
    }

    static {
        // Sanity: never reserve less than firstReservedSubOpcode for unknown
        // bits. Adding a new built-in requires updating both this block and
        // FermataCapabilities#firstReservedSubOpcode() when relevant.
        if (SubOpcodes.SERVER_CAPABILITIES < FermataCapabilities.firstReservedSubOpcode()) {
            throw new IllegalStateException("Built-in Fermata subOpcode overlaps with reserved range");
        }
    }
}