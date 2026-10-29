package l2.gameserver.network.l2.fermata;

import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.fermata.s2c.EconomyV1;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Economy64CodecTest {

    private EmbeddedChannel channel;
    private GameClient client;

    @BeforeEach
    void setUp() {
        channel = new EmbeddedChannel();
        client = new GameClient(new NettyGameConnection(channel));
    }

    @AfterEach
    void tearDown() {
        channel.finishAndReleaseAll();
    }

    @Test
    @DisplayName("EconomyV1: layout serializa envelope 0xFD + subopcode 0x0100 + nativeKind")
    void testEconomyCarrierLayout() {
        byte[] widePayload = new byte[]{1, 2, 3, 4};
        EconomyV1 packet = new EconomyV1(EconomyV1.KIND_BUY_LIST, widePayload);
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(EconomyV1.SUB_OPCODE);
        assertThat(buf.get()).isEqualTo(EconomyV1.KIND_BUY_LIST);
        byte[] data = new byte[4];
        buf.get(data);
        assertThat(data).containsExactly(1, 2, 3, 4);
    }

    @Test
    @DisplayName("EconomyWireHelper: Tag 0 para itens normais e Tag 1 para bilhete de Monster Race (4443)")
    void testMetadataTags() {
        ByteBuffer buf = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);

        // Item normal (ex: Adena 57) -> Tag 0 (1 byte)
        EconomyWireHelper.writeItemMetadata(buf, 57, 0, 0, 0L);
        assertThat(buf.position()).isEqualTo(1);
        buf.flip();
        assertThat(buf.get()).isEqualTo((byte) 0);

        buf.clear();
        // Monster Race Ticket (4443) -> Tag 1 (1 + 2 + 2 + 8 = 13 bytes)
        EconomyWireHelper.writeItemMetadata(buf, 4443, 10, 5, 5_000_000_000L);
        assertThat(buf.position()).isEqualTo(13);
        buf.flip();
        assertThat(buf.get()).isEqualTo((byte) 1);
        assertThat(buf.getShort()).isEqualTo((short) 10);
        assertThat(buf.getShort()).isEqualTo((short) 5);
        assertThat(buf.getLong()).isEqualTo(5_000_000_000L);
    }

    @Test
    @DisplayName("Aritmética segura 64-bit: detecta overflow de contagem sem truncamento 32-bit")
    void testCheckedArithmetic() {
        long hugeAmount = 5_000_000_000L; // > 2^31 - 1
        assertThat(hugeAmount).isGreaterThan(Integer.MAX_VALUE);

        long doubled = EconomyWireHelper.safeMultiply(hugeAmount, 2L);
        assertThat(doubled).isEqualTo(10_000_000_000L);

        assertThatThrownBy(() -> EconomyWireHelper.safeMultiply(Long.MAX_VALUE, 2L))
                .isInstanceOf(ArithmeticException.class);

        assertThatThrownBy(() -> EconomyWireHelper.safeAdd(Long.MAX_VALUE, 1L))
                .isInstanceOf(ArithmeticException.class);
    }

    private byte[] serialize(L2GameServerPacket packet) {
        ByteBuffer buf = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);
        boolean ok = packet.writeTo(buf, client);
        assertThat(ok).isTrue();
        buf.flip();
        byte[] out = new byte[buf.remaining()];
        buf.get(out);
        return out;
    }
}
