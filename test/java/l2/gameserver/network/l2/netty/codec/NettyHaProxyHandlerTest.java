package l2.gameserver.network.l2.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NettyHaProxyHandlerTest {

    @Test
    @DisplayName("Deve detectar header PROXY protocol v2 IPv4 e extrair o IP real")
    void testProxyProtocolV2IPv4() {
        NettyHaProxyHandler handler = new NettyHaProxyHandler();
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        NettyGameConnection conn = new NettyGameConnection(channel);

        // Assinatura PROXY v2 (12 bytes) + ver/cmd (0x21) + fam/proto (0x11 = IPv4 TCP) + len (12 bytes)
        // + src IP (192.168.1.100) + dst IP (10.0.0.1) + src port (12345) + dst port (7777)
        byte[] proxyHeader = new byte[] {
            // 12 bytes signature
            (byte) 0x0D, (byte) 0x0A, (byte) 0x0D, (byte) 0x0A,
            (byte) 0x00, (byte) 0x0D, (byte) 0x0A, (byte) 0x51,
            (byte) 0x55, (byte) 0x49, (byte) 0x54, (byte) 0x0A,
            // ver/cmd: 0x21 (v2, PROXY command)
            (byte) 0x21,
            // fam/proto: 0x11 (AF_INET, STREAM)
            (byte) 0x11,
            // length: 12 bytes de dados de endereço (0x000C)
            (byte) 0x00, (byte) 0x0C,
            // src IP: 192.168.1.100
            (byte) 192, (byte) 168, (byte) 1, (byte) 100,
            // dst IP: 10.0.0.1
            (byte) 10, (byte) 0, (byte) 0, (byte) 1,
            // src port: 12345 (0x3039)
            (byte) 0x30, (byte) 0x39,
            // dst port: 7777 (0x1E61)
            (byte) 0x1E, (byte) 0x61,
            // Seguido por bytes de jogo (payload normal)
            (byte) 0xAA, (byte) 0xBB
        };

        channel.writeInbound(Unpooled.wrappedBuffer(proxyHeader));

        // O IP deve ter sido extraído e gravado na conexão
        assertThat(conn.getIpAddr()).isEqualTo("192.168.1.100");

        // O handler deve ter se removido do pipeline
        assertThat(channel.pipeline().get(NettyHaProxyHandler.class)).isNull();

        // Os bytes seguintes de jogo devem ter passado direto
        ByteBuf remaining = channel.readInbound();
        assertThat(remaining).isNotNull();
        assertThat(remaining.readableBytes()).isEqualTo(2);
        assertThat(remaining.readByte()).isEqualTo((byte) 0xAA);
        assertThat(remaining.readByte()).isEqualTo((byte) 0xBB);
        remaining.release();
    }

    @Test
    @DisplayName("Deve passar direto e remover-se quando conexão não for PROXY protocol")
    void testDirectConnectionNonProxy() {
        NettyHaProxyHandler handler = new NettyHaProxyHandler();
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        NettyGameConnection conn = new NettyGameConnection(channel);

        // Pacote normal de Lineage II (sem assinatura PROXY v2)
        byte[] normalL2Packet = new byte[] { 0x0E, 0x00, 0x00, 0x01, 0x02 };

        channel.writeInbound(Unpooled.wrappedBuffer(normalL2Packet));

        // Handler se removeu do pipeline
        assertThat(channel.pipeline().get(NettyHaProxyHandler.class)).isNull();

        // Os bytes do pacote L2 permanecem intactos para o próximo decodificador
        ByteBuf read = channel.readInbound();
        assertThat(read).isNotNull();
        byte[] result = new byte[read.readableBytes()];
        read.readBytes(result);
        assertThat(result).containsExactly(normalL2Packet);
        read.release();
    }
}
