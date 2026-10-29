package l2.gameserver.network.l2.fermata;

import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.fermata.s2c.EquipmentPreview;
import l2.gameserver.network.l2.fermata.s2c.EquipmentPreview.StatDiff;
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

class EquipmentPreviewCodecTest {

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
    @DisplayName("EquipmentPreview: layout wire exato com 0xFD envelope, stat diffs e replaced items")
    void testEquipmentPreviewLayout() {
        List<StatDiff> diffs = List.of(
                new StatDiff(1, 100, 150),
                new StatDiff(2, 200, 250)
        );
        int[] replaced = new int[]{1010, 1020};
        List<int[]> skills = List.of(new int[]{3001, 1});

        EquipmentPreview packet = new EquipmentPreview(
                5001, 7, EquipmentPreview.RESULT_OK,
                (byte) 5, (byte) 1, diffs, replaced, skills
        );

        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(EquipmentPreview.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(5001);
        assertThat(buf.get() & 0xFF).isEqualTo(7);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(EquipmentPreview.RESULT_OK);
        assertThat(buf.get()).isEqualTo((byte) 5);
        assertThat(buf.get()).isEqualTo((byte) 1);

        assertThat(buf.getShort() & 0xFFFF).isEqualTo(2); // statCount
        assertThat(buf.get() & 0xFF).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(100);
        assertThat(buf.getInt()).isEqualTo(150);
        assertThat(buf.get() & 0xFF).isEqualTo(2);
        assertThat(buf.getInt()).isEqualTo(200);
        assertThat(buf.getInt()).isEqualTo(250);

        assertThat(buf.get() & 0xFF).isEqualTo(2); // replacedCount
        assertThat(buf.getInt()).isEqualTo(1010);
        assertThat(buf.getInt()).isEqualTo(1020);

        assertThat(buf.get() & 0xFF).isEqualTo(1); // skillCount
        assertThat(buf.getInt()).isEqualTo(3001);
        assertThat(buf.get() & 0xFF).isEqualTo(1);
    }

    @Test
    @DisplayName("EquipmentPreview: failure() emite zero arrays sem payload fantasma")
    void testEquipmentPreviewFailure() {
        EquipmentPreview fail = EquipmentPreview.failure(999, 1, EquipmentPreview.RESULT_NOT_EQUIPPABLE);
        ByteBuffer buf = ByteBuffer.wrap(serialize(fail)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(EquipmentPreview.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(999);
        assertThat(buf.get() & 0xFF).isEqualTo(1);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(EquipmentPreview.RESULT_NOT_EQUIPPABLE);
        assertThat(buf.get()).isEqualTo((byte) 0);
        assertThat(buf.get()).isEqualTo((byte) 0);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(0); // 0 stat diffs
        assertThat(buf.get() & 0xFF).isEqualTo(0);     // 0 replaced
        assertThat(buf.get() & 0xFF).isEqualTo(0);     // 0 skills
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
