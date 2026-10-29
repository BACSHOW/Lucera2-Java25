package l2.gameserver.network.l2.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.netty.codec.L2PacketEncoder;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class NettyBatchSendingTest {
    private static final class FlushCounter extends ChannelOutboundHandlerAdapter {
        private final AtomicInteger flushes = new AtomicInteger();

        @Override
        public void flush(ChannelHandlerContext ctx) {
            this.flushes.incrementAndGet();
            ctx.flush();
        }
    }

    private static final class ValuePacket extends L2GameServerPacket {
        private final int value;

        private ValuePacket(int value) {
            this.value = value;
        }

        @Override
        protected void writeImpl() {
            writeC(0x7A);
            writeD(this.value);
        }
    }

    @Test
    void sendsPacketsInOrderWithOneFlush() {
        FlushCounter flushCounter = new FlushCounter();
        EmbeddedChannel channel = new EmbeddedChannel(flushCounter, new L2PacketEncoder());
        NettyGameConnection connection = new NettyGameConnection(channel);
        GameClient client = new GameClient(connection);
        connection.attachClient(client);

        client.sendPackets(List.of(new ValuePacket(11), new ValuePacket(22), new ValuePacket(33)));
        channel.runPendingTasks();

        assertThat(flushCounter.flushes).hasValue(1);
        for (int expected : List.of(11, 22, 33)) {
            ByteBuf frame = channel.readOutbound();
            assertThat(frame).isNotNull();
            assertThat(frame.readUnsignedShortLE()).isEqualTo(7);
            assertThat((int) frame.readUnsignedByte()).isEqualTo(0x7A);
            assertThat(frame.readIntLE()).isEqualTo(expected);
            frame.release();
        }
        ByteBuf extra = channel.readOutbound();
        assertThat(extra).isNull();
        channel.finishAndReleaseAll();
    }

    @Test
    void explicitBatchScopeDefersFlushUntilScopeCloses() {
        FlushCounter flushCounter = new FlushCounter();
        EmbeddedChannel channel = new EmbeddedChannel(flushCounter, new L2PacketEncoder());
        NettyGameConnection connection = new NettyGameConnection(channel);
        GameClient client = new GameClient(connection);
        connection.attachClient(client);

        client.startPacketBatch();
        client.sendPacket(new ValuePacket(1));
        client.sendPacket(new ValuePacket(2));
        assertThat(flushCounter.flushes).hasValue(0);
        client.endPacketBatch();
        assertThat(flushCounter.flushes).hasValue(1);
        channel.runPendingTasks();
        ByteBuf first = channel.readOutbound();
        ByteBuf second = channel.readOutbound();
        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        first.release();
        second.release();
        channel.finishAndReleaseAll();
    }

    @Test
    void recordsOutboundPacketAudit() {
        EmbeddedChannel channel = new EmbeddedChannel(new L2PacketEncoder());
        NettyGameConnection connection = new NettyGameConnection(channel);
        GameClient client = new GameClient(connection);
        connection.attachClient(client);

        client.sendPacket(new ValuePacket(12));
        assertThat(client.getPacketAuditTrail().snapshot())
            .extracting(entry -> entry.packetType())
            .contains("ValuePacket");
        channel.finishAndReleaseAll();
    }

    @Test
    void ignoresNullPacketsWithinBatch() {
        FlushCounter flushCounter = new FlushCounter();
        EmbeddedChannel channel = new EmbeddedChannel(flushCounter, new L2PacketEncoder());
        NettyGameConnection connection = new NettyGameConnection(channel);
        GameClient client = new GameClient(connection);
        connection.attachClient(client);

        client.sendPacket(new ValuePacket(7), null, new ValuePacket(9));
        channel.runPendingTasks();

        assertThat(flushCounter.flushes).hasValue(1);
        for (int expected : List.of(7, 9)) {
            ByteBuf frame = channel.readOutbound();
            assertThat(frame).isNotNull();
            frame.skipBytes(3);
            assertThat(frame.readIntLE()).isEqualTo(expected);
            frame.release();
        }
        ByteBuf extra = channel.readOutbound();
        assertThat(extra).isNull();
        channel.finishAndReleaseAll();
    }

    @Test
    void preservesSequentialEncryptionWithinBatch() {
        EmbeddedChannel channel = new EmbeddedChannel(new L2PacketEncoder());
        NettyGameConnection connection = new NettyGameConnection(channel);
        GameClient client = new GameClient(connection);
        connection.attachClient(client);
        client.enableCrypt();

        client.sendPackets(List.of(new ValuePacket(1), new ValuePacket(2), new ValuePacket(3)));
        channel.runPendingTasks();

        int packetIndex = 0;
        for (int expected : List.of(1, 2, 3)) {
            ByteBuf frame = channel.readOutbound();
            assertThat(frame).isNotNull();
            int payloadLength = frame.readUnsignedShortLE() - L2PacketEncoder.HEADER_SIZE;
            byte[] payload = new byte[payloadLength];
            frame.readBytes(payload);
            frame.release();

            if (packetIndex++ > 0) {
                ByteBuffer decrypted = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN);
                client.decrypt(decrypted, payloadLength);
            }
            assertThat(ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN).get() & 0xFF).isEqualTo(0x7A);
            assertThat(ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN).getInt(1)).isEqualTo(expected);
        }
        channel.finishAndReleaseAll();
    }

    @Test
    void closePacketIsFlushedBeforeChannelCloses() {
        FlushCounter flushCounter = new FlushCounter();
        EmbeddedChannel channel = new EmbeddedChannel(flushCounter, new L2PacketEncoder());
        NettyGameConnection connection = new NettyGameConnection(channel);
        GameClient client = new GameClient(connection);
        connection.attachClient(client);

        client.close(new ValuePacket(44));
        channel.runPendingTasks();
        channel.runPendingTasks();

        assertThat(flushCounter.flushes).hasValue(1);
        ByteBuf frame = channel.readOutbound();
        assertThat(frame).isNotNull();
        assertThat(frame.readUnsignedShortLE()).isEqualTo(7);
        frame.skipBytes(1);
        assertThat(frame.readIntLE()).isEqualTo(44);
        frame.release();
        assertThat(channel.isActive()).isFalse();
        channel.finishAndReleaseAll();
    }
}
