package l2.gameserver.network.l2.fermata;

import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.fermata.s2c.CoordinationResult;
import l2.gameserver.network.l2.fermata.s2c.CoordinationSnapshot;
import l2.gameserver.network.l2.fermata.s2c.LinkedChat;
import l2.gameserver.network.l2.fermata.s2c.LinkedItemChat;
import l2.gameserver.network.l2.fermata.s2c.PartyPing;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SocialCoordinationCodecTest {

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
    @DisplayName("LinkedItemChat: roundtrip deve produzir layout S2C determinístico com 0xFD envelope")
    void testLinkedItemChatRoundtrip() {
        List<FermataItemSnapshot> items = List.of(
                new FermataItemSnapshot(57, 57, 0, 0, 0, 0, 0, 0, false, 1000),
                new FermataItemSnapshot(135, 135, 0, 5, 0, 0, 0, 0, false, 1)
        );

        LinkedItemChat packet = LinkedItemChat.fromSnapshots(42, "Hero", 3, "hello world", items);
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(LinkedItemChat.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(42); // senderObjectId
        assertThat(readUtf16Z(buf)).isEqualTo("Hero");
        assertThat(buf.getInt()).isEqualTo(3); // chatType
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(2); // itemCount
        buf.position(buf.position() + 32 * 2);
        assertThat(readUtf16Z(buf)).isEqualTo("hello world");
    }

    @Test
    @DisplayName("LinkedItemChat: payload total cresce linearmente com itemCount")
    void testLinkedItemChatSizeLinearInItems() {
        byte[] empty = serialize(LinkedItemChat.fromSnapshots(1, "x", 0, "m", List.of()));
        byte[] full = serialize(LinkedItemChat.fromSnapshots(1, "x", 0, "m", List.of(
                new FermataItemSnapshot(1, 1, 0, 0, 0, 0, 0, 0, false, 1),
                new FermataItemSnapshot(2, 2, 0, 0, 0, 0, 0, 0, false, 1),
                new FermataItemSnapshot(3, 3, 0, 0, 0, 0, 0, 0, false, 1),
                new FermataItemSnapshot(4, 4, 0, 0, 0, 0, 0, 0, false, 1))));
        assertThat(full.length - empty.length)
                .as("4 itens devem somar 4 * 32 bytes a mais que 0 itens")
                .isEqualTo(4 * FermataItemSnapshot.SNAPSHOT_SIZE);
    }

    @Test
    @DisplayName("FermataItemSnapshot: deve serializar layout 32 bytes little-endian consistente")
    void testSnapshotBinaryLayout() {
        FermataItemSnapshot snapshot = new FermataItemSnapshot(135, 135, 0x1234, 15, 0, 0, 1234, 5678, true, 1);
        ByteBuffer buf = ByteBuffer.allocate(FermataItemSnapshot.SNAPSHOT_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        snapshot.writeTo(buf);
        buf.flip();

        assertThat(buf.remaining()).isEqualTo(FermataItemSnapshot.SNAPSHOT_SIZE);
        assertThat(buf.getInt()).isEqualTo(135);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(15);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1234);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(5678);
        int flags = buf.getInt();
        assertThat(flags & 1).isEqualTo(1); // FLAG_EQUIPPED
        assertThat(buf.get() & 0xFF).isEqualTo(0);
        assertThat(buf.get() & 0xFF).isEqualTo(0);
        assertThat(buf.get() & 0xFF).isEqualTo(0);
        assertThat(buf.get() & 0xFF).isEqualTo(0);
        assertThat(buf.get() & 0xFF).isEqualTo(0);
        assertThat(buf.get() & 0xFF).isEqualTo(0);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(0x1234);
        assertThat(buf.getInt()).isEqualTo(135); // visibleItemId
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // count
    }

    @Test
    @DisplayName("LinkedChat: deve emitir snapshots de item + skills no payload")
    void testLinkedChatDualLinks() {
        List<FermataItemSnapshot> items = List.of(new FermataItemSnapshot(6580, 6580, 0, 10, 0, 0, 0, 0, false, 1));
        List<int[]> skills = List.of(new int[]{1218, 30, 0});

        LinkedChat packet = LinkedChat.fromDirect(77, "Bishop", 0, "heal me", items, skills);
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(LinkedChat.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(77);
        assertThat(readUtf16Z(buf)).isEqualTo("Bishop");
        assertThat(buf.getInt()).isEqualTo(0);

        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // itemCount
        buf.position(buf.position() + 32);

        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // skillCount
        assertThat(buf.getInt()).isEqualTo(1218);
        assertThat(buf.getInt()).isEqualTo(30);
        assertThat(buf.getInt()).isEqualTo(0); // subLevel

        assertThat(readUtf16Z(buf)).isEqualTo("heal me");
    }

    @Test
    @DisplayName("PartyPing: layout binário completo e campos respeitados")
    void testPartyPingLayout() {
        PartyPing ping = new PartyPing(101, false, 0, 1000, 2000, 3000, 5000, 2);
        ByteBuffer buf = ByteBuffer.wrap(serialize(ping)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(PartyPing.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(101);
        assertThat(buf.get() & 0xFF).isEqualTo(1); // coords ping
        assertThat(buf.getInt()).isEqualTo(0);     // targetId nulo
        assertThat(buf.getInt()).isEqualTo(1000);
        assertThat(buf.getInt()).isEqualTo(2000);
        assertThat(buf.getInt()).isEqualTo(3000);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(5000);
        assertThat(buf.get() & 0xFF).isEqualTo(2);
    }

    @Test
    @DisplayName("PartyPing: duração deve ser clampada para [500, 30000] ms")
    void testPartyPingDurationClamp() {
        ByteBuffer low = ByteBuffer.wrap(serialize(new PartyPing(1, false, 0, 0, 0, 0, -100, 0)))
                .order(ByteOrder.LITTLE_ENDIAN);
        low.position(1 + 2 + 4 + 1 + 4 + 4 + 4 + 4); // 0xFD(1) + op(2) + sender(4) + kind(1) + target(4) + x,y,z(12)
        assertThat(low.getShort() & 0xFFFF).isEqualTo(500);

        ByteBuffer high = ByteBuffer.wrap(serialize(new PartyPing(1, false, 0, 0, 0, 0, 1_000_000, 0)))
                .order(ByteOrder.LITTLE_ENDIAN);
        high.position(1 + 2 + 4 + 1 + 4 + 4 + 4 + 4);
        assertThat(high.getShort() & 0xFFFF).isEqualTo(30_000);
    }

    @Test
    @DisplayName("CoordinationSnapshot: layout com 2 beacons e label UTF-16LE")
    void testCoordinationSnapshotLayout() {
        CoordinationSnapshot snap = new CoordinationSnapshot(
                555, 0x12345678, 2,
                new byte[]{1, 2},
                new int[]{10, 20},
                new int[]{30, 40},
                new int[]{50, 60},
                new byte[]{3, 4},
                new int[]{1000, 2000},
                "Boss spot"
        );
        ByteBuffer buf = ByteBuffer.wrap(serialize(snap)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(CoordinationSnapshot.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(555);
        assertThat(buf.getInt()).isEqualTo(0x12345678);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(2);

        assertThat(buf.get() & 0xFF).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(10);
        assertThat(buf.getInt()).isEqualTo(30);
        assertThat(buf.getInt()).isEqualTo(50);
        assertThat(buf.get() & 0xFF).isEqualTo(3);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1000);

        assertThat(buf.get() & 0xFF).isEqualTo(2);
        assertThat(buf.getInt()).isEqualTo(20);
        assertThat(buf.getInt()).isEqualTo(40);
        assertThat(buf.getInt()).isEqualTo(60);
        assertThat(buf.get() & 0xFF).isEqualTo(4);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(2000);

        assertThat(readUtf16Z(buf)).isEqualTo("Boss spot");
    }

    @Test
    @DisplayName("CoordinationResult: layout de confirmação com correlationId e count")
    void testCoordinationResultLayout() {
        CoordinationResult res = new CoordinationResult(0xAABBCCDD, 3);
        ByteBuffer buf = ByteBuffer.wrap(serialize(res)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(CoordinationResult.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(0xAABBCCDD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(3);
    }

    @Test
    @DisplayName("Rate limit de item link chat: máximo 4 links na janela")
    void testRateLimitItemLinkChat() {
        int playerId = 99991;
        for (int i = 0; i < FermataSocialCoordination.ITEM_LINK_LIMIT; i++) {
            assertThat(FermataSocialCoordination.allowItemLinkChat(playerId)).isTrue();
        }
        assertThat(FermataSocialCoordination.allowItemLinkChat(playerId)).isFalse();
    }

    @Test
    @DisplayName("Rate limit de party ping: máximo 6 pings por janela")
    void testRateLimitPartyPing() {
        int playerId = 99992;
        for (int i = 0; i < FermataSocialCoordination.PARTY_PING_LIMIT; i++) {
            assertThat(FermataSocialCoordination.allowPartyPing(playerId)).isTrue();
        }
        assertThat(FermataSocialCoordination.allowPartyPing(playerId)).isFalse();
    }

    @Test
    @DisplayName("Rate limit de coordenação tática: máximo 3 mensagens na janela")
    void testRateLimitCoordination() {
        int playerId = 99993;
        for (int i = 0; i < FermataSocialCoordination.COORDINATION_LIMIT; i++) {
            assertThat(FermataSocialCoordination.allowCoordination(playerId)).isTrue();
        }
        assertThat(FermataSocialCoordination.allowCoordination(playerId)).isFalse();
    }

    @Test
    @DisplayName("Limites rígidos do protocolo")
    void testProtocolConstants() {
        assertThat(LinkedItemChat.MAX_LINKED_ITEMS).isEqualTo(4);
        assertThat(LinkedChat.MAX_ITEMS).isEqualTo(4);
        assertThat(LinkedChat.MAX_SKILLS).isEqualTo(4);
        assertThat(CoordinationSnapshot.MAX_BEACONS).isEqualTo(8);
    }

    // ---------- helpers ----------

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
