package l2.gameserver.network.l2.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class L2FrameDecoderTest {

    @Test
    @DisplayName("Deve decodificar frame completo Little-Endian corretamente")
    void testCompleteFrame() {
        EmbeddedChannel channel = new EmbeddedChannel(new L2FrameDecoder());

        // Frame total = 7 bytes (2 bytes header + 5 bytes payload)
        // Little endian: 7 = 0x0007 -> low: 0x07, high: 0x00
        byte[] data = new byte[] { 0x07, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05 };
        boolean written = channel.writeInbound(Unpooled.wrappedBuffer(data));

        assertThat(written).isTrue();

        ByteBuf decoded = channel.readInbound();
        assertThat(decoded).isNotNull();
        assertThat(decoded.readableBytes()).isEqualTo(5);

        byte[] payload = new byte[5];
        decoded.readBytes(payload);
        assertThat(payload).containsExactly(0x01, 0x02, 0x03, 0x04, 0x05);
        decoded.release();
    }

    @Test
    @DisplayName("Deve lidar com pacotes TCP fragmentados (aguardando buffer)")
    void testFragmentedFrame() {
        EmbeddedChannel channel = new EmbeddedChannel(new L2FrameDecoder());

        // Frame total = 6 bytes (2 bytes header + 4 bytes body)
        // Envia apenas o header de 2 bytes primeiro
        boolean written1 = channel.writeInbound(Unpooled.wrappedBuffer(new byte[] { 0x06, 0x00 }));
        assertThat(written1).isFalse(); // Nada emitido ainda
        assertThat((ByteBuf) channel.readInbound()).isNull();

        // Envia 2 bytes do corpo
        boolean written2 = channel.writeInbound(Unpooled.wrappedBuffer(new byte[] { 0x10, 0x20 }));
        assertThat(written2).isFalse(); // Ainda faltam 2 bytes
        assertThat((ByteBuf) channel.readInbound()).isNull();

        // Envia os últimos 2 bytes
        boolean written3 = channel.writeInbound(Unpooled.wrappedBuffer(new byte[] { 0x30, 0x40 }));
        assertThat(written3).isTrue();

        ByteBuf decoded = channel.readInbound();
        assertThat(decoded).isNotNull();
        assertThat(decoded.readableBytes()).isEqualTo(4);

        byte[] payload = new byte[4];
        decoded.readBytes(payload);
        assertThat(payload).containsExactly(0x10, 0x20, 0x30, 0x40);
        decoded.release();
    }

    @Test
    @DisplayName("Deve separar múltiplos pacotes colados na mesma leitura TCP")
    void testCoalescedFrames() {
        EmbeddedChannel channel = new EmbeddedChannel(new L2FrameDecoder());

        // Pacote 1: total = 4 bytes (2 header + 2 body: [0xAA, 0xBB])
        // Pacote 2: total = 5 bytes (2 header + 3 body: [0x01, 0x02, 0x03])
        byte[] combined = new byte[] {
            0x04, 0x00, (byte) 0xAA, (byte) 0xBB,
            0x05, 0x00, 0x01, 0x02, 0x03
        };

        boolean written = channel.writeInbound(Unpooled.wrappedBuffer(combined));
        assertThat(written).isTrue();

        ByteBuf p1 = channel.readInbound();
        assertThat(p1).isNotNull();
        assertThat(p1.readableBytes()).isEqualTo(2);
        assertThat(p1.readByte()).isEqualTo((byte) 0xAA);
        assertThat(p1.readByte()).isEqualTo((byte) 0xBB);
        p1.release();

        ByteBuf p2 = channel.readInbound();
        assertThat(p2).isNotNull();
        assertThat(p2.readableBytes()).isEqualTo(3);
        assertThat(p2.readByte()).isEqualTo((byte) 0x01);
        assertThat(p2.readByte()).isEqualTo((byte) 0x02);
        assertThat(p2.readByte()).isEqualTo((byte) 0x03);
        p2.release();
    }

    @Test
    @DisplayName("Deve rejeitar pacote com tamanho inválido (< 2 bytes)")
    void testInvalidFrameSize() {
        EmbeddedChannel channel = new EmbeddedChannel(new L2FrameDecoder());

        // Total = 1 byte (inválido porque HEADER_SIZE é 2)
        byte[] invalid = new byte[] { 0x01, 0x00 };
        assertThatThrownBy(() -> channel.writeInbound(Unpooled.wrappedBuffer(invalid)))
            .hasRootCauseInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Illegal L2 frame size");
    }

    @Test
    @DisplayName("Deve descartar frame vazio sem produzir pacote")
    void testEmptyFrameIsSuppressed() {
        EmbeddedChannel channel = new EmbeddedChannel(new L2FrameDecoder());
        assertThat(channel.writeInbound(Unpooled.wrappedBuffer(new byte[] { 0x02, 0x00 }))).isFalse();
        assertThat((ByteBuf) channel.readInbound()).isNull();
    }
}
