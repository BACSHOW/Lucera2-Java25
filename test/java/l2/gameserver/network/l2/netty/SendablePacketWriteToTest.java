package l2.gameserver.network.l2.netty;

import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

class SendablePacketWriteToTest {

    static class TestDummyPacket extends L2GameServerPacket {
        private final int opcode = 0x01;
        private final int val = 42;

        @Override
        protected void writeImpl() {
            writeC(this.opcode);
            writeD(this.val);
            writeS("LuceraNetty");
        }
    }

    @Test
    @DisplayName("Deve serializar pacote via writeTo sem depender de SelectorThread")
    void testWriteToExternalBuffer() {
        EmbeddedChannel channel = new EmbeddedChannel();
        NettyGameConnection conn = new NettyGameConnection(channel);
        GameClient client = new GameClient(conn);

        TestDummyPacket packet = new TestDummyPacket();
        ByteBuffer buf = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);

        boolean success = packet.writeTo(buf, client);
        assertThat(success).isTrue();

        buf.flip();
        assertThat(buf.get() & 0xFF).isEqualTo(0x01); // opcode
        assertThat(buf.getInt()).isEqualTo(42);         // int val

        // Lê a String UTF-16LE
        StringBuilder sb = new StringBuilder();
        char c;
        while ((c = buf.getChar()) != '\0') {
            sb.append(c);
        }
        assertThat(sb.toString()).isEqualTo("LuceraNetty");
    }
}
