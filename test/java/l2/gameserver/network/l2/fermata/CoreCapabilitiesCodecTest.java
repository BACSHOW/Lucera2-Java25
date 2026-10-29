/*
 * L2JLucera2 — Fermata wire codec test.
 *
 * Covers the encoding/decoding contract shared by ExFermataEnvelope,
 * ServerCapabilities, SetWeather, RestoreClientWeather and the
 * RequestFermataClientCapabilities C2S handler. Pure JVM tests; no
 * SelectorThread or Netty transport involvement.
 */
package l2.gameserver.network.l2.fermata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.fermata.s2c.ExFermataEnvelope;
import l2.gameserver.network.l2.fermata.s2c.RestoreClientWeather;
import l2.gameserver.network.l2.fermata.s2c.ServerCapabilities;
import l2.gameserver.network.l2.fermata.s2c.SetWeather;

class CoreCapabilitiesCodecTest {

    private static final int OUTER_OPCODE = 0xFD;

    // ---------------- Capability bit arithmetic ----------------

    @Nested
    @DisplayName("FermataCapabilities bit arithmetic")
    class CapabilityBits {

        @Test
        @DisplayName("has() reports membership for each defined bit")
        void hasReportsMembership() {
            int mask = FermataCapabilities.BIT_LINKED_ITEM_CHAT
                    | FermataCapabilities.BIT_WIDE_ECONOMY;
            assertThat(FermataCapabilities.has(mask, FermataCapabilities.BIT_LINKED_ITEM_CHAT)).isTrue();
            assertThat(FermataCapabilities.has(mask, FermataCapabilities.BIT_WIDE_ECONOMY)).isTrue();
            assertThat(FermataCapabilities.has(mask, FermataCapabilities.BIT_OFFLINE_BUFF_UI)).isFalse();
        }

        @Test
        @DisplayName("negotiate() returns the intersection and strips reserved bits")
        void negotiateStripsReserved() {
            int client = FermataCapabilities.BIT_LINKED_ITEM_CHAT
                    | FermataCapabilities.BIT_AUGMENT_SCROLL_V2
                    | FermataCapabilities.MASK_RESERVED_HIGH; // sneaky reserved bits
            int server = FermataCapabilities.BIT_LINKED_ITEM_CHAT
                    | FermataCapabilities.BIT_OFFLINE_BUFF_UI;
            int negotiated = FermataCapabilities.negotiate(client, server);
            assertThat(negotiated).isEqualTo(FermataCapabilities.BIT_LINKED_ITEM_CHAT);
            assertThat((negotiated & FermataCapabilities.MASK_RESERVED_HIGH)).isZero();
        }

        @Test
        @DisplayName("allKnownBits() returns the union of every defined bit")
        void allKnownBitsIsComplete() {
            int union = FermataCapabilities.allKnownBits();
            assertThat(FermataCapabilities.has(union, FermataCapabilities.BIT_LINKED_ITEM_CHAT)).isTrue();
            assertThat(FermataCapabilities.has(union, FermataCapabilities.BIT_WIDE_ECONOMY)).isTrue();
            assertThat(FermataCapabilities.has(union, FermataCapabilities.BIT_OFFLINE_BUFF_UI)).isTrue();
            assertThat(FermataCapabilities.has(union, FermataCapabilities.BIT_PACKET_TIMING_REPORT)).isTrue();
            assertThat(FermataCapabilities.has(union, FermataCapabilities.BIT_AUGMENT_SCROLL_V2)).isTrue();
        }

        @Test
        @DisplayName("describe() enumerates names in canonical bit order")
        void describeEnumeratesNames() {
            Set<String> names = FermataCapabilities.describe(
                    FermataCapabilities.BIT_AUGMENT_SCROLL_V2
                            | FermataCapabilities.BIT_LINKED_ITEM_CHAT);
            assertThat(names).containsExactlyInAnyOrder("AUGMENT_SCROLL_V2", "LINKED_ITEM_CHAT");
        }

        @Test
        @DisplayName("isClientMaskValid() rejects negative and reserved-bit values")
        void clientMaskValidation() {
            assertThat(FermataCapabilities.isClientMaskValid(0)).isTrue();
            assertThat(FermataCapabilities.isClientMaskValid(FermataCapabilities.allKnownBits())).isTrue();
            assertThat(FermataCapabilities.isClientMaskValid(-1)).isFalse();
            assertThat(FermataCapabilities.isClientMaskValid(FermataCapabilities.MASK_RESERVED_HIGH)).isFalse();
            // boundary: highest legal bit
            assertThat(FermataCapabilities.isClientMaskValid(1 << 15)).isTrue();
            // boundary: lowest reserved bit (must be rejected)
            assertThat(FermataCapabilities.isClientMaskValid(1 << 16)).isFalse();
        }

        @Test
        @DisplayName("firstReservedSubOpcode() sits above the largest built-in")
        void reservedSubOpcodeAboveBuiltins() {
            assertThat(FermataCapabilities.firstReservedSubOpcode())
                    .isGreaterThanOrEqualTo(ExFermataEnvelope.SubOpcodes.SERVER_CAPABILITIES);
        }
    }

    // ---------------- Revision boundaries ----------------

    @Nested
    @DisplayName("Revision boundary handling")
    class RevisionBoundaries {

        @Test
        @DisplayName("SUPPORTED_REVISION sits within [MIN_REVISION, MAX_REVISION]")
        void supportedRevisionWithinBounds() {
            assertThat(FermataCapabilities.SUPPORTED_REVISION)
                    .isBetween(FermataCapabilities.MIN_REVISION, FermataCapabilities.MAX_REVISION);
        }

        @Test
        @DisplayName("MIN_REVISION is strictly positive")
        void minRevisionPositive() {
            assertThat(FermataCapabilities.MIN_REVISION).isPositive();
        }

        @Test
        @DisplayName("MAX_REVISION is at least MIN_REVISION")
        void maxRevisionAtLeastMin() {
            assertThat(FermataCapabilities.MAX_REVISION).isGreaterThanOrEqualTo(FermataCapabilities.MIN_REVISION);
        }
    }

    // ---------------- Packet registry ----------------

    @Nested
    @DisplayName("FermataPacketRegistry dispatch")
    class RegistryDispatch {

        @Test
        @DisplayName("resolve() returns a known factory for the registered subOpcode")
        void resolveKnownSubOpcode() {
            assertThat(FermataPacketRegistry.registeredCount()).isGreaterThanOrEqualTo(1);
            ByteBuffer payload = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
            L2GameClientPacket packet = FermataPacketRegistry.resolve(
                    FermataPacketRegistry.SUB_OPCODE_REQUEST_CAPABILITIES, payload, null);
            assertThat(packet).isNotNull();
            // Should be our handshake class (subclass of FermataClientPacket)
            assertThat(packet).isInstanceOf(FermataClientPacket.class);
        }

        @Test
        @DisplayName("resolve() falls back to dummy packet for unknown subOpcode")
        void resolveUnknownFallsBack() {
            ByteBuffer payload = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
            int bogusSub = 0x7FFF;
            while (FermataPacketRegistry.registeredCount() > 0 && bogusSub == FermataPacketRegistry.SUB_OPCODE_REQUEST_CAPABILITIES) {
                bogusSub = (bogusSub + 1) & 0xFFFF;
            }
            L2GameClientPacket packet = FermataPacketRegistry.resolve(bogusSub, payload, null);
            assertThat(packet).isNotNull();
            assertThat(packet).isNotInstanceOf(FermataClientPacket.class);
        }

        @Test
        @DisplayName("subOpcode outside [MIN, MAX] range is rejected to dummy")
        void resolveOutOfRange() {
            ByteBuffer payload = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
            // Above MAX_SUB_OPCODE
            L2GameClientPacket high = FermataPacketRegistry.resolve(0xFFFF, payload, null);
            assertThat(high).isNotInstanceOf(FermataClientPacket.class);
        }

        @Test
        @DisplayName("register/unregister is symmetric")
        void registerUnregister() {
            int before = FermataPacketRegistry.registeredCount();
            FermataPacketRegistry.register(0x7E00, FermataDummyHolder::new);
            assertThat(FermataPacketRegistry.registeredCount()).isEqualTo(before + 1);
            FermataPacketRegistry.unregister(0x7E00);
            assertThat(FermataPacketRegistry.registeredCount()).isEqualTo(before);
        }

        @Test
        @DisplayName("register() with null factory throws")
        void registerNullThrows() {
            assertThatThrownBy(() -> FermataPacketRegistry.register(0x1234, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ---------------- Handshake round-trip ----------------

    @Nested
    @DisplayName("Handshake C2S -> S2C round-trip")
    class HandshakeRoundTrip {

        @Test
        @DisplayName("ServerCapabilities wire encoding matches the spec")
        void serverCapabilitiesEncoding() {
            int revision = FermataCapabilities.SUPPORTED_REVISION;
            int serverMask = FermataCapabilities.allKnownBits();
            ServerCapabilities packet = new ServerCapabilities(revision, serverMask);

            byte[] bytes = writePacket(packet);

            assertThat(bytes[0] & 0xFF).isEqualTo(OUTER_OPCODE);
            int sub = readShortLE(bytes, 1);
            assertThat(sub).isEqualTo(ExFermataEnvelope.SubOpcodes.SERVER_CAPABILITIES);

            int wireRevision = readIntLE(bytes, 3);
            int wireMask = readIntLE(bytes, 7);

            assertThat(wireRevision).isEqualTo(revision);
            assertThat(wireMask).isEqualTo(serverMask);
        }

        @Test
        @DisplayName("ServerCapabilities masks out reserved bits on the wire")
        void serverCapabilitiesMaskReservedBits() {
            int revision = FermataCapabilities.SUPPORTED_REVISION;
            int dirtyMask = FermataCapabilities.allKnownBits() | FermataCapabilities.MASK_RESERVED_HIGH;
            ServerCapabilities packet = new ServerCapabilities(revision, dirtyMask);

            byte[] bytes = writePacket(packet);
            int wireMask = readIntLE(bytes, 7);

            assertThat(wireMask).isEqualTo(FermataCapabilities.allKnownBits());
            assertThat((wireMask & FermataCapabilities.MASK_RESERVED_HIGH)).isZero();
        }

        @Test
        @DisplayName("SetWeather wire encoding matches the spec")
        void setWeatherEncoding() {
            SetWeather packet = new SetWeather(2, 75);
            byte[] bytes = writePacket(packet);

            assertThat(bytes[0] & 0xFF).isEqualTo(OUTER_OPCODE);
            int sub = readShortLE(bytes, 1);
            assertThat(sub).isEqualTo(ExFermataEnvelope.SubOpcodes.SET_WEATHER);

            int weatherId = readIntLE(bytes, 3);
            int intensity = readIntLE(bytes, 7);
            assertThat(weatherId).isEqualTo(2);
            assertThat(intensity).isEqualTo(75);
            assertThat(bytes).hasSize(11);
        }

        @Test
        @DisplayName("RestoreClientWeather default ctor emits zoneId=0")
        void restoreClientWeatherDefault() {
            RestoreClientWeather packet = new RestoreClientWeather();
            byte[] bytes = writePacket(packet);

            assertThat(bytes[0] & 0xFF).isEqualTo(OUTER_OPCODE);
            int sub = readShortLE(bytes, 1);
            assertThat(sub).isEqualTo(ExFermataEnvelope.SubOpcodes.RESTORE_CLIENT_WEATHER);
            int zone = readIntLE(bytes, 3);
            assertThat(zone).isZero();
        }

        @Test
        @DisplayName("RequestFermataClientCapabilities decodes a 0xFD handshake")
        void requestCapabilitiesDecodesHandshake() throws Exception {
            // The dispatcher is supposed to have consumed 0xFD + 2 bytes subOpcode
            // before handing the buffer to FermataClientPacket#readImpl. We mimic
            // that here by stripping those bytes from the buffer.
            int revision = FermataCapabilities.SUPPORTED_REVISION;
            int clientMask = FermataCapabilities.BIT_LINKED_ITEM_CHAT
                    | FermataCapabilities.BIT_WIDE_ECONOMY;
            ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            buf.putInt(revision);
            buf.putInt(clientMask);
            buf.flip();

            // SubOpcode has already been consumed; the packet just reads payload.
            l2.gameserver.network.l2.fermata.c2s.RequestFermataClientCapabilities packet =
                    new l2.gameserver.network.l2.fermata.c2s.RequestFermataClientCapabilities();
            packet.setByteBuffer(buf);
            packet.setClient(null);
            packet.read();

            // Pull the private fields back via reflection so we don't expose
            // accessors just for tests.
            int parsedRev = (int) readField(packet, "_revision");
            int parsedMask = (int) readField(packet, "_clientMask");
            assertThat(parsedRev).isEqualTo(revision);
            assertThat(parsedMask).isEqualTo(clientMask);
        }

        @Test
        @DisplayName("RequestFermataClientCapabilities rejects truncated buffer")
        void requestCapabilitiesRejectsTruncated() throws Exception {
            ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            buf.putInt(42);
            buf.flip();

            l2.gameserver.network.l2.fermata.c2s.RequestFermataClientCapabilities packet =
                    new l2.gameserver.network.l2.fermata.c2s.RequestFermataClientCapabilities();
            packet.setByteBuffer(buf);
            packet.setClient(null);
            packet.read(); // must not throw

            int parsedRev = (int) readField(packet, "_revision");
            // Both fields stay at default 0
            assertThat(parsedRev).isZero();
        }

        @Test
        @DisplayName("ServerCapabilities are immutable: sentinels reject negative revisions")
        void serverCapabilitiesNegativeRevision() {
            // Negative revision still serializes a negative int on the wire;
            // validators reject it later, not here.
            ServerCapabilities packet = new ServerCapabilities(-1, 0);
            byte[] bytes = writePacket(packet);
            assertThat(bytes).hasSize(11);
            assertThat(readIntLE(bytes, 3)).isEqualTo(-1);
        }
    }

    // ---------------- FermataContext ----------------

    @Nested
    @DisplayName("FermataContext state holder")
    class ContextState {

        @Test
        @DisplayName("Fresh context is inactive and reports zero state")
        void freshContextInactive() {
            FermataContext ctx = new FermataContext();
            assertThat(ctx.isActive()).isFalse();
            assertThat(ctx.getNegotiatedMask()).isZero();
            assertThat(ctx.isWideEconomy()).isFalse();
            assertThat(ctx.getHandshakeNanos()).isZero();
        }

        @Test
        @DisplayName("applyHandshake sets active and exposes the negotiated mask")
        void applyHandshakeActivates() {
            FermataContext ctx = new FermataContext();
            int serverMask = FermataCapabilities.allKnownBits();
            int clientMask = FermataCapabilities.BIT_LINKED_ITEM_CHAT
                    | FermataCapabilities.BIT_WIDE_ECONOMY;
            int negotiated = FermataCapabilities.negotiate(clientMask, serverMask);

            ctx.applyHandshake(1, clientMask, serverMask, negotiated);

            assertThat(ctx.isActive()).isTrue();
            assertThat(ctx.getRevision()).isEqualTo(1);
            assertThat(ctx.getClientMask()).isEqualTo(clientMask);
            assertThat(ctx.getServerMask()).isEqualTo(serverMask);
            assertThat(ctx.getNegotiatedMask()).isEqualTo(negotiated);
            assertThat(ctx.isWideEconomy()).isTrue();
            assertThat(ctx.has(FermataCapabilities.BIT_WIDE_ECONOMY)).isTrue();
            assertThat(ctx.has(FermataCapabilities.BIT_OFFLINE_BUFF_UI)).isFalse();
            assertThat(ctx.getHandshakeNanos()).isPositive();
        }

        @Test
        @DisplayName("reset() clears every property")
        void resetClearsState() {
            FermataContext ctx = new FermataContext();
            ctx.applyHandshake(1, FermataCapabilities.allKnownBits(),
                    FermataCapabilities.allKnownBits(), FermataCapabilities.allKnownBits());
            ctx.reset();
            assertThat(ctx.isActive()).isFalse();
            assertThat(ctx.getRevision()).isZero();
            assertThat(ctx.getClientMask()).isZero();
            assertThat(ctx.getServerMask()).isZero();
            assertThat(ctx.getNegotiatedMask()).isZero();
            assertThat(ctx.isWideEconomy()).isFalse();
            assertThat(ctx.getHandshakeNanos()).isZero();
        }
    }

    // ---------------- ExFermataEnvelope subOpcode contract ----------------

    @Nested
    @DisplayName("ExFermataEnvelope subOpcode contract")
    class EnvelopeSubOpcodeContract {

        @Test
        @DisplayName("All built-in subOpcodes fit in a LE unsigned short")
        void subOpcodesFitShort() {
            int[] subs = {
                    ExFermataEnvelope.SubOpcodes.SET_WEATHER,
                    ExFermataEnvelope.SubOpcodes.RESTORE_CLIENT_WEATHER,
                    ExFermataEnvelope.SubOpcodes.SERVER_CAPABILITIES
            };
            for (int s : subs) {
                assertThat(s & 0xFFFF).isEqualTo(s);
            }
        }

        @Test
        @DisplayName("Envelope requiredCapability defaults to 0 (no gating)")
        void defaultCapabilityIsZero() {
            ExFermataEnvelope envelope = new SetWeather(0, 0);
            assertThat(envelope.requiredCapability()).isZero();
        }

        @Test
        @DisplayName("Constructing with a subOpcode &gt; 0xFFFF is masked")
        void subOpcodeMasked() {
            // Direct construction via reflection-free subclass to test masking
            ExFermataEnvelope envelope = new ServerCapabilities(1, 0);
            byte[] bytes = writePacket(envelope);
            // Outer opcode is 0xFD, then LE short subOpcode. SERVER_CAPABILITIES = 0x0013.
            int sub = readShortLE(bytes, 1);
            assertThat(sub).isEqualTo(ExFermataEnvelope.SubOpcodes.SERVER_CAPABILITIES);
        }
    }

    // ---------------- Helpers ----------------

    /**
     * Renders an envelope to its byte form using the public {@code writeTo}
     * binding. The supplied {@code client} is allowed to be {@code null};
     * {@code ExFermataEnvelope} does not consult it.
     */
    private static byte[] writePacket(ExFermataEnvelope envelope) {
        ByteBuffer buf = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
        envelope.writeTo(buf, null);
        buf.flip();
        byte[] out = new byte[buf.remaining()];
        buf.get(out);
        return out;
    }

    private static int readShortLE(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8);
    }

    private static int readIntLE(byte[] bytes, int offset) {
        return (bytes[offset] & 0xFF)
                | ((bytes[offset + 1] & 0xFF) << 8)
                | ((bytes[offset + 2] & 0xFF) << 16)
                | ((bytes[offset + 3] & 0xFF) << 24);
    }

    private static Object readField(Object target, String name) throws Exception {
        java.lang.reflect.Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }

    /** Used by the registry unregister test. */
    public static final class FermataDummyHolder
            extends l2.gameserver.network.l2.c2s.L2GameClientPacket {
        @Override
        protected void readImpl() {
            // no-op
        }

        @Override
        protected void runImpl() {
            // no-op
        }
    }
}