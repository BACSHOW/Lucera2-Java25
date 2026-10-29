package l2.gameserver.network.l2.fermata;

import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.fermata.s2c.CombatRecordResponse;
import l2.gameserver.network.l2.fermata.s2c.FightLinkChat;
import l2.gameserver.network.l2.fermata.s2c.ReportTransferResponse;
import l2.gameserver.network.l2.fermata.s2c.WorldEntryIdentity;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CombatMeterReportsCodecTest {

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
    @DisplayName("WorldEntryIdentity: wire layout matches revision 1 + 16 bytes uuid + objectId")
    void testWorldEntryIdentityLayout() {
        UUID uuid = UUID.randomUUID();
        WorldEntryIdentity packet = new WorldEntryIdentity(uuid, 123456);
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(WorldEntryIdentity.SUB_OPCODE);
        assertThat(buf.get()).isEqualTo((byte) 1); // revision
        byte[] uuidBytes = new byte[16];
        buf.get(uuidBytes);
        assertThat(uuidBytes).hasSize(16);
        assertThat(buf.getInt()).isEqualTo(123456); // characterObjectId
    }

    @Test
    @DisplayName("CombatRecordResponse: refusal e stop encodam envelope 0xFD e subopcode 0x0101")
    void testCombatRecordResponseLayout() {
        CombatRecordResponse refusal = CombatRecordResponse.refusal(999L, (byte) 3);
        ByteBuffer buf = ByteBuffer.wrap(serialize(refusal)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(CombatRecordResponse.SUB_OPCODE);
        assertThat(buf.get()).isEqualTo((byte) 1); // revision
        assertThat(buf.get()).isEqualTo(CombatRecordResponse.KIND_REFUSAL);
        assertThat(buf.getLong()).isEqualTo(999L);
        assertThat(buf.get()).isEqualTo((byte) 3);
    }

    @Test
    @DisplayName("ReportTransferResponse: failed encoda envelope 0xFD e subopcode 0x0102")
    void testReportTransferResponseLayout() {
        ReportTransferResponse failed = ReportTransferResponse.failed(777L, (byte) 6);
        ByteBuffer buf = ByteBuffer.wrap(serialize(failed)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(ReportTransferResponse.SUB_OPCODE);
        assertThat(buf.get()).isEqualTo((byte) 1);
        assertThat(buf.get()).isEqualTo(ReportTransferResponse.KIND_FAILED);
        assertThat(buf.getLong()).isEqualTo(777L);
        assertThat(buf.get()).isEqualTo((byte) 6);
    }

    @Test
    @DisplayName("FightLinkChat: layout serializa revision 1, speaker, template e bytes")
    void testFightLinkChatLayout() {
        FightLinkChat packet = new FightLinkChat(123, 0, "Gladiator", "Check this fight", new byte[]{1, 2, 3});
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(FightLinkChat.SUB_OPCODE);
        assertThat(buf.get()).isEqualTo((byte) 1); // revision
        assertThat(buf.getInt()).isEqualTo(123);
        assertThat(buf.getInt()).isEqualTo(0);
        assertThat(readUtf16Z(buf)).isEqualTo("Gladiator");
        assertThat(readUtf16Z(buf)).isEqualTo("Check this fight");
        byte[] ref = new byte[3];
        buf.get(ref);
        assertThat(ref).containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("FermataCombatQuantizer: converte HP para 2^-20 com precisão exata")
    void testQuantization() {
        double original = 1500.5;
        long quantized = FermataCombatQuantizer.quantizeHp(original);
        double unquantized = FermataCombatQuantizer.unquantizeHp(quantized);
        assertThat(unquantized).isCloseTo(original, org.assertj.core.data.Offset.offset(0.00001));
    }

    private static String readUtf16Z(ByteBuffer buf) {
        StringBuilder sb = new StringBuilder();
        char c;
        while ((c = buf.getChar()) != '\u0000') sb.append(c);
        return sb.toString();
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
