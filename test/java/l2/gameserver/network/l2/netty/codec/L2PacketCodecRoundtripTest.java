package l2.gameserver.network.l2.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class L2PacketCodecRoundtripTest {

    @Test
    @DisplayName("Deve codificar e decodificar pacote em passthrough antes do handshake de criptografia")
    void testPassthroughRoundtrip() {
        L2PacketEncoder encoder = new L2PacketEncoder();
        L2PacketDecoder decoder = new L2PacketDecoder();
        EmbeddedChannel channel = new EmbeddedChannel(encoder, decoder);

        NettyGameConnection conn = new NettyGameConnection(channel);
        GameClient client = new GameClient(conn);
        conn.attachClient(client);

        // Envia pacote de saída
        byte[] originalPayload = new byte[] { 0x00, 0x01, 0x02, (byte) 0xFF };
        channel.writeOutbound(Unpooled.wrappedBuffer(originalPayload));

        ByteBuf encodedWire = channel.readOutbound();
        assertThat(encodedWire).isNotNull();
        // Wire format: 2 bytes header + 4 bytes payload = 6 bytes
        assertThat(encodedWire.readableBytes()).isEqualTo(6);
        int totalLength = encodedWire.readByte() & 0xFF | ((encodedWire.readByte() & 0xFF) << 8);
        assertThat(totalLength).isEqualTo(6);

        // O restante dos bytes é o corpo enviado para o decoder de entrada
        ByteBuf body = encodedWire.readRetainedSlice(4);
        channel.writeInbound(body);

        ByteBuf decoded = channel.readInbound();
        assertThat(decoded).isNotNull();
        byte[] received = new byte[decoded.readableBytes()];
        decoded.readBytes(received);

        assertThat(received).containsExactly(originalPayload);
        encodedWire.release();
        decoded.release();
    }

    @Test
    @DisplayName("Deve codificar e decodificar após ativação da chave Blowfish / XOR")
    void testEncryptedRoundtrip() {
        L2PacketEncoder encoder = new L2PacketEncoder();
        L2PacketDecoder decoder = new L2PacketDecoder();
        EmbeddedChannel channel = new EmbeddedChannel(encoder, decoder);

        NettyGameConnection conn = new NettyGameConnection(channel);
        GameClient client = new GameClient(conn);
        conn.attachClient(client);

        // Ativa criptografia
        client.enableCrypt();

        // Primeiro pacote de saída arma a flag aN no GameCrypt
        byte[] firstPacket = new byte[] { 0x2E, 0x01, 0x00, 0x00 }; // Ex: VersionCheck / KeyPacket
        channel.writeOutbound(Unpooled.wrappedBuffer(firstPacket));
        ByteBuf wire1 = channel.readOutbound();
        wire1.release();

        // Segundo pacote de saída: já deve estar devidamente encriptado
        byte[] secondPacket = new byte[] { 0x11, 0x22, 0x33, 0x44, 0x55 };
        channel.writeOutbound(Unpooled.wrappedBuffer(secondPacket));
        ByteBuf wire2 = channel.readOutbound();
        assertThat(wire2).isNotNull();
        assertThat(wire2.readableBytes()).isEqualTo(secondPacket.length + 2);

        // Pula os 2 bytes de header (que é responsabilidade do L2FrameDecoder)
        wire2.skipBytes(2);
        ByteBuf bodyToDecrypt = wire2.readRetainedSlice(secondPacket.length);

        channel.writeInbound(bodyToDecrypt);
        ByteBuf decrypted = channel.readInbound();
        assertThat(decrypted).isNotNull();

        byte[] roundtrip = new byte[decrypted.readableBytes()];
        decrypted.readBytes(roundtrip);
        assertThat(roundtrip).containsExactly(secondPacket);

        wire2.release();
        decrypted.release();
    }
}
